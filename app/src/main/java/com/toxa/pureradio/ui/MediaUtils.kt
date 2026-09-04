package com.toxa.pureradio.ui

import androidx.compose.ui.graphics.Color
import com.toxa.pureradio.R
import java.net.URLDecoder
import java.net.URLEncoder

object MediaUtils {
    @Volatile
    private var appIconBytesCache: ByteArray? = null

    /** URL-encode a genre/country name so it can be safely embedded in a MediaItem ID. */
    fun encodeBrowseName(name: String): String = URLEncoder.encode(name, "UTF-8")

    /** Decode a genre/country name that was embedded in a MediaItem ID. */
    fun decodeBrowseName(encoded: String): String = URLDecoder.decode(encoded, "UTF-8")

    fun getAppIconArtworkBytes(context: android.content.Context): ByteArray? {
        appIconBytesCache?.let { return it }
        return try {
            val drawable = context.resources.getDrawable(R.drawable.ic_radio_logo, context.theme)
            val width = drawable.intrinsicWidth.coerceAtLeast(1)
            val height = drawable.intrinsicHeight.coerceAtLeast(1)
            val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            val stream = java.io.ByteArrayOutputStream()
            if (bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)) {
                stream.toByteArray().also { appIconBytesCache = it }
            } else null
        } catch (_: Exception) { null }
    }

    fun getGenreColor(genre: String): Color {
        val hash = genre.hashCode()
        val r = ((Math.abs(hash) % 50) + 10) / 255f
        val g = ((Math.abs(hash shr 8) % 70) + 20) / 255f
        val b = ((Math.abs(hash shr 16) % 100) + 100) / 255f
        return Color(r, g, b)
    }

    fun getGenreImageUrl(genre: String): String {
        val g = genre.lowercase().trim()
        return when {
            g.contains("heavy metal") -> "https://images.unsplash.com/photo-1501612780327-45045538702b?q=80&w=600&auto=format&fit=crop"
            g.contains("metal") -> "https://images.unsplash.com/photo-1598387181032-a3103a2db5b3?q=80&w=600&auto=format&fit=crop"
            g.contains("punk") -> "https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?q=80&w=600&auto=format&fit=crop"
            g.contains("hard rock") -> "https://images.unsplash.com/photo-1454922915609-78549ad709bb?q=80&w=600&auto=format&fit=crop"
            g.contains("classic rock") -> "https://images.unsplash.com/photo-1464375117522-1311d6a5b81f?q=80&w=600&auto=format&fit=crop"
            g.contains("rock") -> "https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?q=80&w=600&auto=format&fit=crop"
            g.contains("alternative") || g.contains("indie") -> "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?q=80&w=600&auto=format&fit=crop"
            g.contains("synthpop") -> "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?q=80&w=600&auto=format&fit=crop"
            g.contains("pop") || g.contains("hits") || g.contains("top") || g.contains("chart") -> "https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?q=80&w=600&auto=format&fit=crop"
            g.contains("smooth jazz") -> "https://images.unsplash.com/photo-1519751138087-5bf79df62d5b?q=80&w=600&auto=format&fit=crop"
            g.contains("jazz") -> "https://images.unsplash.com/photo-1511192336575-5a79af67a629?q=80&w=600&auto=format&fit=crop"
            g.contains("blues") -> "https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?q=80&w=600&auto=format&fit=crop"
            g.contains("soul") -> "https://images.unsplash.com/photo-1460723237483-7a6dc9d0b212?q=80&w=600&auto=format&fit=crop"
            g.contains("funk") || g.contains("disco") -> "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?q=80&w=600&auto=format&fit=crop"
            g.contains("orchestra") || g.contains("symphony") -> "https://images.unsplash.com/photo-1415201364774-f6f0bb35f28f?q=80&w=600&auto=format&fit=crop"
            g.contains("classical") || g.contains("classic") -> "https://images.unsplash.com/photo-1507838153414-b4b713384a76?q=80&w=600&auto=format&fit=crop"
            g.contains("opera") -> "https://images.unsplash.com/photo-1470019693664-1d202d2c0907?q=80&w=600&auto=format&fit=crop"
            g.contains("techno") -> "https://images.unsplash.com/photo-1493676304819-0d7a8d026dcf?q=80&w=600&auto=format&fit=crop"
            g.contains("deep house") -> "https://images.unsplash.com/photo-1557683316-973673baf926?q=80&w=600&auto=format&fit=crop"
            g.contains("house") -> "https://images.unsplash.com/photo-1557683316-973673baf926?q=80&w=600&auto=format&fit=crop"
            g.contains("trance") -> "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?q=80&w=600&auto=format&fit=crop"
            g.contains("psytrance") -> "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?q=80&w=600&auto=format&fit=crop"
            g.contains("electro") || g.contains("edm") -> "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?q=80&w=600&auto=format&fit=crop"
            g.contains("ambient") || g.contains("lofi") -> "https://images.unsplash.com/photo-1516280440614-37939bbacd81?q=80&w=600&auto=format&fit=crop"
            g.contains("chillout") || g.contains("chill") -> "https://images.unsplash.com/photo-1519681393784-d120267933ba?q=80&w=600&auto=format&fit=crop"
            g.contains("lounge") -> "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?q=80&w=600&auto=format&fit=crop"
            g.contains("country") -> "https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?q=80&w=600&auto=format&fit=crop"
            g.contains("bluegrass") -> "https://images.unsplash.com/photo-1510915361894-db8b60106cb1?q=80&w=600&auto=format&fit=crop"
            g.contains("folk") -> "https://images.unsplash.com/photo-1468164016595-6108e4c60c8b?q=80&w=600&auto=format&fit=crop"
            g.contains("hip hop") -> "https://images.unsplash.com/photo-1520262454473-a1a82276a574?q=80&w=600&auto=format&fit=crop"
            g.contains("rap") || g.contains("urban") || g.contains("r&b") -> "https://images.unsplash.com/photo-1524368535928-5b5e00ddc76b?q=80&w=600&auto=format&fit=crop"
            g.contains("reggae") -> "https://images.unsplash.com/photo-1510915228340-29c85a43dcfe?q=80&w=600&auto=format&fit=crop"
            g.contains("ska") -> "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?q=80&w=600&auto=format&fit=crop"
            g.contains("world") -> "https://images.unsplash.com/photo-1526218626217-dc65a29bb444?q=80&w=600&auto=format&fit=crop"
            g.contains("latin") -> "https://images.unsplash.com/photo-1445985543470-41fba5c3144a?q=80&w=600&auto=format&fit=crop"
            g.contains("80s") -> "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?q=80&w=600&auto=format&fit=crop"
            g.contains("90s") -> "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?q=80&w=600&auto=format&fit=crop"
            g.contains("70s") -> "https://images.unsplash.com/photo-1516062423079-7ca13cdc7f5a?q=80&w=600&auto=format&fit=crop"
            g.contains("60s") || g.contains("oldies") || g.contains("retro") -> "https://images.unsplash.com/photo-1461360370896-922624d12aa1?q=80&w=600&auto=format&fit=crop"
            g.contains("soundtrack") || g.contains("movie") || g.contains("film") -> "https://images.unsplash.com/photo-1485846234645-a62644f84728?q=80&w=600&auto=format&fit=crop"
            g.contains("meditation") || g.contains("spiritual") || g.contains("religious") -> "https://images.unsplash.com/photo-1506126613408-eca07ce68773?q=80&w=600&auto=format&fit=crop"
            g.contains("news") || g.contains("talk") || g.contains("info") -> "https://images.unsplash.com/photo-1472289065668-ce650ac443d2?q=80&w=600&auto=format&fit=crop"
            g.contains("sport") -> "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?q=80&w=600&auto=format&fit=crop"
            g.contains("comedy") -> "https://images.unsplash.com/photo-1527224857830-43a7acc85260?q=80&w=600&auto=format&fit=crop"
            g.contains("christmas") || g.contains("xmas") -> "https://images.unsplash.com/photo-1543589077-47d81606c1bf?q=80&w=600&auto=format&fit=crop"
            g.contains("kids") || g.contains("children") -> "https://images.unsplash.com/photo-1516627145497-ae6968895b74?q=80&w=600&auto=format&fit=crop"
            else -> "https://images.unsplash.com/photo-1453090927415-5f45085b65c0?q=80&w=600&auto=format&fit=crop"
        }
    }

    fun getCategoryImageUrl(categoryId: String): String {
        return when (categoryId) {
            "home_screen" -> "https://images.unsplash.com/photo-1487180144351-b8472da7d491?q=80&w=600&auto=format&fit=crop"
            "popular" -> "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?q=80&w=600&auto=format&fit=crop"
            "favourites" -> "https://images.unsplash.com/photo-1499415479124-43c32433a620?q=80&w=600&auto=format&fit=crop"
            "recent" -> "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?q=80&w=600&auto=format&fit=crop"
            "genres" -> "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?q=80&w=600&auto=format&fit=crop"
            "countries" -> "https://images.unsplash.com/photo-1521295121783-8a321d551ad2?q=80&w=600&auto=format&fit=crop"
            else -> "https://images.unsplash.com/photo-1453090927415-5f45085b65c0?q=80&w=600&auto=format&fit=crop"
        }
    }

    fun getCountryFlagUrl(code: String?): String? {
        val c = code?.trim()?.lowercase()
        return if (!c.isNullOrEmpty() && c.length == 2) {
            "https://flagcdn.com/w160/$c.png"
        } else null
    }

    fun getStationArtworkUrl(favicon: String, countryCode: String?): String? {
        val url = favicon.trim().let {
            if (it.startsWith("http://", ignoreCase = true)) {
                it.replaceFirst(Regex("^http://", RegexOption.IGNORE_CASE), "https://")
            } else it
        }
        val uri = runCatching { android.net.Uri.parse(url) }.getOrNull()
        val isValidArtwork = uri?.let {
            it.scheme?.lowercase() in setOf("http", "https") &&
                !it.host.isNullOrBlank() &&
                !it.path.orEmpty().lowercase().endsWith(".ico")
        } == true
        return when {
            isValidArtwork -> url
            !countryCode.isNullOrEmpty() -> getCountryFlagUrl(countryCode)
            else -> null
        }
    }
}
