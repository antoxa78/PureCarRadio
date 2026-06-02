package com.toxa.pureradio.network

import com.toxa.pureradio.data.model.Station
import retrofit2.http.GET
import retrofit2.http.Query

interface RadioBrowserService {
    @GET("json/stations/search")
    suspend fun searchStations(
        @Query("name") name: String? = null,
        @Query("country") country: String? = null,
        @Query("language") language: String? = null,
        @Query("tag") tag: String? = null,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("order") order: String = "votes",
        @Query("reverse") reverse: Boolean = true,
        @Query("hidebroken") hideBroken: Boolean = false
    ): List<Station>

    @GET("json/stations/topclick")
    suspend fun getTopStations(
        @Query("limit") limit: Int = 100,
        @Query("hidebroken") hideBroken: Boolean = false
    ): List<Station>

    @GET("json/tags")
    suspend fun getTags(
        @Query("hidebroken") hideBroken: Boolean = true,
        @Query("order") order: String = "stationcount",
        @Query("reverse") reverse: Boolean = true,
        @Query("limit") limit: Int = 500
    ): List<Tag>

    @GET("json/countries")
    suspend fun getCountries(
        @Query("order") order: String = "stationcount",
        @Query("reverse") reverse: Boolean = true
    ): List<Country>

    @GET("json/stats")
    suspend fun getStats(): ServerStats

    @GET("json/stations/byuuid")
    suspend fun getStationsByUuid(
        @Query("uuids") uuids: String
    ): List<Station>
}

data class ServerStats(
    val stations: Int,
    val stations_broken: Int,
    val tags: Int,
    val countries: Int,
    val languages: Int
)

data class Tag(
    val name: String,
    val stationcount: Int
)

data class Country(
    val name: String,
    val iso_3166_1: String,
    val stationcount: Int
)
