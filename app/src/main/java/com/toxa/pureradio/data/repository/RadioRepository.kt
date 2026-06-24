package com.toxa.pureradio.data.repository

import com.toxa.pureradio.data.model.Station
import com.toxa.pureradio.network.Country
import com.toxa.pureradio.network.RadioBrowserService
import com.toxa.pureradio.network.Tag
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    }

    // Build a Retrofit service for each mirror so we can try them in order.
    private val services: List<RadioBrowserService> = MIRRORS.map { baseUrl ->
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RadioBrowserService::class.java)
    }

    /**
     * Tries [block] on each mirror in order, returning the first successful non-empty
     * result. Falls back to [default] if all mirrors fail or return empty.
     */
    private suspend fun <T> withFallback(default: T, block: suspend (RadioBrowserService) -> T): T {
        for (service in services) {
            try {
                val result = block(service)
                // For collections, only accept non-empty results so we keep trying mirrors.
                if (result is List<*> && result.isEmpty()) continue
                return result
            } catch (e: Exception) {
                // Keep trying next mirror
            }
        }
        // If all mirrors returned empty lists (not exceptions) that is still valid.
        // Re-run on first mirror to return whatever it gives (could be genuinely empty).
        return try {
            block(services.first())
        } catch (e: Exception) {
            default
        }
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
        val results = service.searchStations(
            name = query,
            tag = tag,
            country = country,
            limit = limit,
            offset = offset,
            hideBroken = hideBroken
        )

        val combined = when {
            query != null && tag == null -> {
                val byTag = service.searchStations(
                    tag = query,
                    country = country,
                    limit = limit,
                    offset = offset,
                    hideBroken = hideBroken
                )
                (results + byTag).distinctBy { it.stationUuid }
            }
            tag != null && query == null -> {
                val byName = service.searchStations(
                    name = tag,
                    country = country,
                    limit = limit,
                    offset = offset,
                    hideBroken = hideBroken
                )
                (results + byName).distinctBy { it.stationUuid }
            }
            else -> results
        }

        combined
    }

    suspend fun getStats(): com.toxa.pureradio.network.ServerStats? =
        withFallback(null) { service -> service.getStats() }

    suspend fun getTags(limit: Int = 500): List<Tag> =
        withFallback(emptyList()) { service -> service.getTags(limit = limit) }

    suspend fun getCountries(): List<Country> =
        withFallback(emptyList()) { service -> service.getCountries() }

    suspend fun getStationsByUuid(uuids: String): List<Station> =
        withFallback(emptyList()) { service -> service.getStationsByUuid(uuids) }

    suspend fun getStation(uuid: String): Station? {
        val list = getStationsByUuid(uuid)
        return list.firstOrNull()
    }
}
