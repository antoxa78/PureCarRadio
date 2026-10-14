package com.toxa.pureradio.network

import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/** Latest release published on GitHub. */
data class AppUpdateInfo(
    val version: String,
    val releaseNotes: String,
    val releasePageUrl: String,
    /** Direct link to the attached .apk, if the release has one. */
    val apkUrl: String?
) {
    val downloadUrl: String get() = apkUrl ?: releasePageUrl
}

/**
 * Checks the project's GitHub Releases for a newer version.
 *
 * Uses the public GitHub REST API (no token; 60 requests/hour per IP, plenty for a
 * manual check). Parsed with JsonParser rather than Gson reflection so R8/minify in
 * release builds can't strip the fields.
 */
object UpdateChecker {
    private const val LATEST_RELEASE_URL =
        "https://api.github.com/repos/antoxa78/PureCarRadio/releases/latest"
    const val RELEASES_PAGE_URL = "https://github.com/antoxa78/PureCarRadio/releases"

    /** Fetches the latest release. Throws on network/parse errors. */
    suspend fun fetchLatestRelease(): AppUpdateInfo = withContext(Dispatchers.IO) {
        val connection = (URL(LATEST_RELEASE_URL).openConnection() as HttpURLConnection).apply {
            connectTimeout = 10_000
            readTimeout = 15_000
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("User-Agent", "PureRadio-Android")
        }
        try {
            val code = connection.responseCode
            if (code != HttpURLConnection.HTTP_OK) error("GitHub API returned HTTP $code")
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JsonParser.parseString(body).asJsonObject

            val tag = json.get("tag_name")?.takeIf { !it.isJsonNull }?.asString
                ?: error("Release has no tag")
            val pageUrl = json.get("html_url")?.takeIf { !it.isJsonNull }?.asString ?: RELEASES_PAGE_URL
            val notes = json.get("body")?.takeIf { !it.isJsonNull }?.asString.orEmpty()
            val apkUrl = json.getAsJsonArray("assets")
                ?.map { it.asJsonObject }
                ?.firstOrNull { it.get("name")?.asString?.endsWith(".apk", ignoreCase = true) == true }
                ?.get("browser_download_url")?.asString

            AppUpdateInfo(
                version = normalizeVersion(tag),
                releaseNotes = notes.trim(),
                releasePageUrl = pageUrl,
                apkUrl = apkUrl
            )
        } finally {
            connection.disconnect()
        }
    }

    /** "v1.7.4" / "1.7.4-debug" -> "1.7.4" */
    fun normalizeVersion(version: String): String =
        version.trim().removePrefix("v").removePrefix("V").substringBefore('-').substringBefore(' ')

    /** True if [candidate] is a higher version than [current] (numeric, dot-separated). */
    fun isNewer(candidate: String, current: String): Boolean {
        val a = normalizeVersion(candidate).split('.').map { it.toIntOrNull() ?: 0 }
        val b = normalizeVersion(current).split('.').map { it.toIntOrNull() ?: 0 }
        for (i in 0 until maxOf(a.size, b.size)) {
            val x = a.getOrElse(i) { 0 }
            val y = b.getOrElse(i) { 0 }
            if (x != y) return x > y
        }
        return false
    }
}
