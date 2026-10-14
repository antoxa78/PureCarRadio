package com.toxa.pureradio.data.repository

import com.toxa.pureradio.data.model.Station
import com.toxa.pureradio.network.Country
import com.toxa.pureradio.network.RadioBrowserService
import com.toxa.pureradio.network.Tag
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Repository for Radio Browser API.
 *
 * Radio Browser is a community-driven database accessible through several DNS-round-robin
 * mirrors. If a primary mirror is unreachable we automatically fall back to the next one,
 * making the app resilient to individual server outages.
 *
 * Official mirror list: https://www.radio-browser.info/
 */
class RadioRepository {

    companion object {
        /** Ordered list of Radio Browser API mirrors, most reliable first. */
        private val MIRRORS = listOf(
            "https://de1.api.radio-browser.info/",
            "https://de2.api.radio-browser.info/",
            "https://nl1.api.radio-browser.info/",
            "https://fr1.api.radio-browser.info/"
        )

        // One OkHttpClient shared by every mirror and every RadioRepository instance
        // (the app UI and PlaybackService). Sharing the connection pool means TLS
        // handshakes are reused instead of repeated per mirror/instance, which is the
        // dominant cost on slow or high-latency connections.
        // Short timeouts keep the mirror fallback chain fast so car display browsing
        // doesn't stall and show source errors.
        private val httpClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()
        }

        // Build a Retrofit service for each mirror so we can try them in order.
        private val services: List<RadioBrowserService> by lazy {
            MIRRORS.map { baseUrl ->
                Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(RadioBrowserService::class.java)
            }
        }

        /**
         * Index of the last mirror that answered successfully. Requests start there, so
         * if de1 is down we pay its connect timeout once instead of on every request.
         */
        @Volatile
        private var preferredMirror = 0
    }

    /**
     * Tries [block] on each mirror, starting with the last one that worked, and returns
     * the first successful result. Falls back to [default] if every mirror fails.
     *
     * A successful empty list is a valid answer (e.g. a page past the end of the results)
     * and is returned as-is; previously it triggered a query to every mirror plus one
     * extra retry, i.e. 5 round trips for a single empty page.
     */
    private suspend fun <T> withFallback(default: T, block: suspend (RadioBrowserService) -> T): T {
        val start = preferredMirror
        for (i in services.indices) {
            val index = (start + i) % services.size
            try {
                val result = block(services[index])
                preferredMirror = index
                return result
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Keep trying next mirror
            }
        }
        return default
    }

    suspend fun getTopStations(limit: Int = 100, hideBroken: Boolean = false): List<Station> =
        withFallback(emptyList()) { service ->
            service.getTopStations(limit = limit, hideBroken = hideBroken)
        }

    suspend fun searchStations(
        query: String? = null,
        tag: String? = null,
        country: String? = null,
        limit: Int = 100,
        offset: Int = 0,
        hideBroken: Boolean = false
    ): List<Station> = withFallback(emptyList()) { service ->
        // The primary query and the complementary name/tag query are independent, so
        // run them concurrently instead of back to back.
        coroutineScope {
            val results = async {
                service.searchStations(
                    name = query,
                    tag = tag,
                    country = country,
                    limit = limit,
                    offset = offset,
                    hideBroken = hideBroken
                )
            }

            val extra = when {
                query != null && tag == null -> async {
                    service.searchStations(
                        tag = query,
                        country = country,
                        limit = limit,
                        offset = offset,
                        hideBroken = hideBroken
                    )
                }
                tag != null && query == null -> async {
                    service.searchStations(
                        name = tag,
                        country = country,
                        limit = limit,
                        offset = offset,
                        hideBroken = hideBroken
                    )
                }
                else -> null
            }

            if (extra == null) results.await()
            else (results.await() + extra.await()).distinctBy { it.stationUuid }
        }
    }

    /**
     * Single-request search by tag or country only (no complementary name search).
     * Used for the Home tiles, which only need station counts, so the extra name
     * query would double the download for no visible benefit.
     */
    suspend fun searchStationsSimple(
        tag: String? = null,
        country: String? = null,
        limit: Int = 100,
        offset: Int = 0,
        hideBroken: Boolean = false
    ): List<Station> = withFallback(emptyList()) { service ->
        service.searchStations(
            tag = tag,
            country = country,
            limit = limit,
            offset = offset,
            hideBroken = hideBroken
        )
    }

    suspend fun getStats(): com.toxa.pureradio.network.ServerStats? =
        withFallback(null) { service -> service.getStats() }

    suspend fun getTags(limit: Int = 500): List<Tag> =
        withFallback(emptyList()) { service -> service.getTags(limit = limit) }

    suspend fun getCountries(): List<Country> =
        withFallback(emptyList()) { service -> service.getCountries() }

    suspend fun getStationsByUuid(uuids: String): List<Station> =
        withFallback(emptyList()) { service -> service.getStationsByUuid(uuids) }

    suspend fun getStationsByUrl(url: String): List<Station> =
        withFallback(emptyList()) { service -> service.getStationsByUrl(url) }

    suspend fun getStationsByName(name: String): List<Station> =
        withFallback(emptyList()) { service -> service.getStationsByName(name) }

    suspend fun getStation(uuid: String): Station? {
        val list = getStationsByUuid(uuid)
        return list.firstOrNull()
    }
}
