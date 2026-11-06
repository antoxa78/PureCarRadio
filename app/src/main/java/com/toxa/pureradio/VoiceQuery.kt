package com.toxa.pureradio

import com.toxa.pureradio.data.model.Station
import java.util.Locale

/**
 * Shared helpers for handling spoken queries. Assistant and the on-device
 * recogniser may include command words ("play ...", "find ...") or the app name
 * in the transcribed text, so both the voice playback path and the in-app
 * search field use the same cleanup rules.
 */
object VoiceQuery {

    private val wordSeparator = Regex("[^\\p{L}\\p{N}]+")

    // Longest phrases first so "play the station" wins over "play".
    private val commandPrefixes = listOf(
        "play the radio station",
        "play radio station",
        "play the station",
        "play station",
        "play some",
        "listen to the radio station",
        "listen to",
        "listen",
        "put on",
        "tune in to",
        "tune into",
        "tune to",
        "search for",
        "find",
        "play",
        "open"
    )

    private val appSuffixes = listOf(
        " on the pure radio app",
        " on pure car radio app",
        " on pure radio app",
        " on pure car radio",
        " on pure radio"
    )

    /** Strips command filler that Assistant sometimes includes in the query. */
    fun clean(rawQuery: String): String {
        var query = rawQuery.trim()
            .trim('"', '\'', '“', '”', '‘', '’', '.', ',', '!', '?', ' ')
        if (query.isEmpty()) return query

        val lower = query.lowercase(Locale.ROOT)
        val prefix = commandPrefixes.firstOrNull { lower.startsWith("$it ") }
        if (prefix != null) {
            query = query.substring(prefix.length).trim()
        }

        val lowerQuery = query.lowercase(Locale.ROOT)
        val suffix = appSuffixes.firstOrNull { lowerQuery.endsWith(it) }
        if (suffix != null) {
            query = query.dropLast(suffix.length).trim()
        }
        if (query.endsWith(" please", ignoreCase = true)) {
            query = query.dropLast(" please".length).trim()
        }
        if (query.endsWith(" radio station", ignoreCase = true)) {
            query = query.dropLast(" radio station".length).trim()
        }

        return query.ifBlank { rawQuery.trim() }
    }

    /** Lower-cases and collapses punctuation for fuzzy name comparisons. */
    fun normalize(text: String): String =
        text.lowercase(Locale.ROOT).replace(wordSeparator, " ").trim()

    private fun nameScore(
        name: String,
        query: String,
        exactScore: Int,
        prefixScore: Int
    ): Int {
        if (query.isBlank()) return 100
        return when {
            name == query -> exactScore
            name.startsWith(query) -> prefixScore
            query.startsWith(name) && name.length >= 4 -> prefixScore + 1
            name.contains(query) -> 3
            else -> 100
        }
    }

    private fun withoutLeadingArticle(name: String): String = when {
        name.startsWith("the ") -> name.removePrefix("the ")
        name.startsWith("a ") -> name.removePrefix("a ")
        name.startsWith("an ") -> name.removePrefix("an ")
        else -> name
    }

    /**
     * Relevance of a station for a spoken or typed query. Lower is better:
     *  -2/-1: exact/prefix match against the original phrase (e.g. "Play Radio")
     *    0/1: exact/prefix match against the cleaned phrase
     *      3: name contains the query
     *      4: most words appear in the name
     *      5/6: tag/country match
     *      7: unrelated (listed last)
     */
    fun matchScore(station: Station, rawQuery: String, cleanQuery: String = clean(rawQuery)): Int {
        val name = normalize(station.name)
        val raw = normalize(rawQuery)
        val clean = normalize(cleanQuery)

        var best = 100
        // A station actually named "Play Radio" should beat stations that only
        // match the cleaned query "radio".
        if (raw.isNotBlank() && raw != clean) {
            best = minOf(best, nameScore(name, raw, exactScore = -2, prefixScore = -1))
        }
        if (clean.isNotBlank()) {
            best = minOf(best, nameScore(name, clean, exactScore = 0, prefixScore = 1))
            // "The Wave" should match "Wave" as well as a station literally
            // named "Wave" does.
            val nameWithoutArticle = withoutLeadingArticle(name)
            if (nameWithoutArticle != name) {
                best = minOf(
                    best,
                    nameScore(nameWithoutArticle, clean, exactScore = 0, prefixScore = 1)
                )
            }
        }
        if (best < 100) return best

        val queryTokens = clean.split(' ').filter { it.length > 1 }
        if (queryTokens.isNotEmpty()) {
            val nameTokens = name.split(' ')
            val matched = queryTokens.count { token -> nameTokens.any { it.startsWith(token) } }
            if (matched * 4 >= queryTokens.size * 3) return 4
        }
        if (clean.isNotBlank() && normalize(station.tags).contains(clean)) return 5
        if (clean.isNotBlank() && normalize(station.country).contains(clean)) return 6
        return 7
    }

    /**
     * Sorts stations so exact/near name matches come before only tag or country
     * matches. Ties keep the Radio Browser vote order.
     */
    fun rank(
        stations: List<Station>,
        rawQuery: String,
        cleanQuery: String = clean(rawQuery)
    ): List<Station> = stations.sortedWith(
        compareBy<Station> { matchScore(it, rawQuery, cleanQuery) }
            .thenByDescending { it.votes }
    )
}
