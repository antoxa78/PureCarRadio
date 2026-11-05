package com.toxa.pureradio.ui.viewmodel

import android.content.SharedPreferences
import com.toxa.pureradio.data.model.Station

enum class BitrateFilter {
    Low, High, FLAC
}

/** True when the station is lossless, using the same heuristics as the phone UI. */
fun Station.isLossless(): Boolean =
    codec.equals("FLAC", ignoreCase = true) ||
        tags.contains("flac", ignoreCase = true) ||
        name.contains("flac", ignoreCase = true) ||
        bitrate >= 900

/** Applies the selected bitrate chips to a station list. An empty set matches everything. */
fun matchesBitrateFilter(station: Station, bitrates: Set<BitrateFilter>): Boolean {
    if (bitrates.isEmpty()) return true
    val br = station.bitrate
    val isFlac = station.isLossless()
    return (bitrates.contains(BitrateFilter.Low) && br < 192 && !isFlac) ||
        (bitrates.contains(BitrateFilter.High) && br >= 192 && !isFlac) ||
        (bitrates.contains(BitrateFilter.FLAC) && isFlac)
}

/** Reads the bitrate selection saved by the phone / Android Automotive UI. */
fun readBitrateFilters(prefs: SharedPreferences): Set<BitrateFilter> {
    val saved = prefs.getStringSet("bitrate_filters", null) ?: return emptySet()
    return saved.mapNotNull {
        try {
            BitrateFilter.valueOf(it)
        } catch (e: Exception) {
            null
        }
    }.toSet()
}

/** Stable cache key component for a bitrate selection. */
fun bitrateFilterKey(bitrates: Set<BitrateFilter>): String =
    bitrates.map { it.name }.sorted().joinToString(",")
