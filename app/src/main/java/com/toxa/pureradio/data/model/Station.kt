package com.toxa.pureradio.data.model

import com.google.gson.annotations.SerializedName

data class Station(
    @SerializedName("stationuuid") val stationUuid: String,
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String,
    @SerializedName("favicon") val favicon: String,
    @SerializedName("tags") val tags: String,
    @SerializedName("country") val country: String,
    @SerializedName("countrycode") val countryCode: String? = null,
    @SerializedName("language") val language: String,
    @SerializedName("votes") val votes: Int,
    @SerializedName("codec") val codec: String? = "",
    @SerializedName("bitrate") val bitrate: Int
)
