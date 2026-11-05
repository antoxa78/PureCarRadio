package com.toxa.pureradio.ui

import androidx.compose.ui.graphics.Color
import com.toxa.pureradio.R
import java.net.URLDecoder
import java.net.URLEncoder

object MediaUtils {
    @Volatile
    private var appIconBytesCache: ByteArray? = null

    @Volatile
    private var appIconBitmapCache: android.graphics.Bitmap? = null

    /** URL-encode a genre/country name so it can be safely embedded in a MediaItem ID. */
    fun encodeBrowseName(name: String): String = URLEncoder.encode(name, "UTF-8")

    /** Decode a genre/country name that was embedded in a MediaItem ID. */
    fun decodeBrowseName(encoded: String): String = URLDecoder.decode(encoded, "UTF-8")

    fun getAppIconBitmap(context: android.content.Context): android.graphics.Bitmap? {
        appIconBitmapCache?.let { return it }
        val bytes = getAppIconArtworkBytes(context) ?: return null
        return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.also {
            appIconBitmapCache = it
        }
    }

    fun getAppIconArtworkBytes(context: android.content.Context): ByteArray? {
        appIconBytesCache?.let { return it }
        return try {
            val drawable = androidx.appcompat.content.res.AppCompatResources.getDrawable(
                context,
                R.drawable.ic_radio_logo
            ) ?: return null
            val width = drawable.intrinsicWidth.coerceAtLeast(1)
            val height = drawable.intrinsicHeight.coerceAtLeast(1)
            val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            val stream = java.io.ByteArrayOutputStream()
            if (bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)) {
                val bytes = stream.toByteArray()
                appIconBytesCache = bytes
                bytes
            } else null
        } catch (_: Exception) { null }
    }

    fun getGenreColor(genre: String): Color {
        val hue = (stableGenreHash(genre) % 360).toInt()
        return Color.hsl(hue.toFloat(), 0.55f, 0.24f)
    }

    /**
     * One curated photo per exact genre name. Matching by exact name (instead of the old
     * "contains" rules) prevents several different categories from sharing the same image.
     * Every other category gets its own generated artwork in [getGenreImageUrl].
     */
    private val featuredGenreImages = mapOf(
        "rock" to "https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?q=80&w=600&auto=format&fit=crop",
        "pop" to "https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?q=80&w=600&auto=format&fit=crop",
        "jazz" to "https://images.unsplash.com/photo-1511192336575-5a79af67a629?q=80&w=600&auto=format&fit=crop",
        "electronic" to "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?q=80&w=600&auto=format&fit=crop",
        "news" to "https://images.unsplash.com/photo-1472289065668-ce650ac443d2?q=80&w=600&auto=format&fit=crop",
        "classical" to "https://images.unsplash.com/photo-1507838153414-b4b713384a76?q=80&w=600&auto=format&fit=crop",
        "heavy metal" to "https://images.unsplash.com/photo-1501612780327-45045538702b?q=80&w=600&auto=format&fit=crop",
        "metal" to "https://images.unsplash.com/photo-1598387181032-a3103a2db5b3?q=80&w=600&auto=format&fit=crop",
        "punk" to "https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?q=80&w=600&auto=format&fit=crop",
        "hard rock" to "https://images.unsplash.com/photo-1454922915609-78549ad709bb?q=80&w=600&auto=format&fit=crop",
        "classic rock" to "https://images.unsplash.com/photo-1464375117522-1311d6a5b81f?q=80&w=600&auto=format&fit=crop",
        "alternative" to "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?q=80&w=600&auto=format&fit=crop",
        "synthpop" to "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?q=80&w=600&auto=format&fit=crop",
        "smooth jazz" to "https://images.unsplash.com/photo-1519751138087-5bf79df62d5b?q=80&w=600&auto=format&fit=crop",
        "blues" to "https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?q=80&w=600&auto=format&fit=crop",
        "soul" to "https://images.unsplash.com/photo-1460723237483-7a6dc9d0b212?q=80&w=600&auto=format&fit=crop",
        "funk" to "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?q=80&w=600&auto=format&fit=crop",
        "orchestra" to "https://images.unsplash.com/photo-1415201364774-f6f0bb35f28f?q=80&w=600&auto=format&fit=crop",
        "opera" to "https://images.unsplash.com/photo-1470019693664-1d202d2c0907?q=80&w=600&auto=format&fit=crop",
        "techno" to "https://images.unsplash.com/photo-1493676304819-0d7a8d026dcf?q=80&w=600&auto=format&fit=crop",
        "house" to "https://images.unsplash.com/photo-1557683316-973673baf926?q=80&w=600&auto=format&fit=crop",
        "trance" to "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?q=80&w=600&auto=format&fit=crop",
        "ambient" to "https://images.unsplash.com/photo-1516280440614-37939bbacd81?q=80&w=600&auto=format&fit=crop",
        "lounge" to "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?q=80&w=600&auto=format&fit=crop",
        "country" to "https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?q=80&w=600&auto=format&fit=crop",
        "bluegrass" to "https://images.unsplash.com/photo-1510915361894-db8b60106cb1?q=80&w=600&auto=format&fit=crop",
        "folk" to "https://images.unsplash.com/photo-1468164016595-6108e4c60c8b?q=80&w=600&auto=format&fit=crop",
        "hip hop" to "https://images.unsplash.com/photo-1520262454473-a1a82276a574?q=80&w=600&auto=format&fit=crop",
        "rap" to "https://images.unsplash.com/photo-1524368535928-5b5e00ddc76b?q=80&w=600&auto=format&fit=crop",
        "reggae" to "https://images.unsplash.com/photo-1510915228340-29c85a43dcfe?q=80&w=600&auto=format&fit=crop",
        "world" to "https://images.unsplash.com/photo-1526218626217-dc65a29bb444?q=80&w=600&auto=format&fit=crop",
        "latin" to "https://images.unsplash.com/photo-1445985543470-41fba5c3144a?q=80&w=600&auto=format&fit=crop",
        "80s" to "https://images.unsplash.com/photo-1519681393784-d120267933ba?q=80&w=600&auto=format&fit=crop",
        "90s" to "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?q=80&w=600&auto=format&fit=crop",
        "70s" to "https://images.unsplash.com/photo-1516062423079-7ca13cdc7f5a?q=80&w=600&auto=format&fit=crop",
        "60s" to "https://images.unsplash.com/photo-1461360370896-922624d12aa1?q=80&w=600&auto=format&fit=crop",
        "soundtrack" to "https://images.unsplash.com/photo-1485846234645-a62644f84728?q=80&w=600&auto=format&fit=crop",
        "meditation" to "https://images.unsplash.com/photo-1506126613408-eca07ce68773?q=80&w=600&auto=format&fit=crop",
        "sport" to "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?q=80&w=600&auto=format&fit=crop",
        "comedy" to "https://images.unsplash.com/photo-1527224857830-43a7acc85260?q=80&w=600&auto=format&fit=crop",
        "christmas" to "https://images.unsplash.com/photo-1543589077-47d81606c1bf?q=80&w=600&auto=format&fit=crop",
        "kids" to "https://images.unsplash.com/photo-1516627145497-ae6968895b74?q=80&w=600&auto=format&fit=crop"
    )

    /**
     * Radio Browser exposes hundreds of tag names. Curated photos cover the common ones,
     * while every other category gets a deterministic, unique abstract artwork generated
     * from its name. This prevents the same fallback photo appearing on multiple tiles.
     */
    fun getGenreImageUrl(genre: String): String {
        val key = genre.trim().lowercase()
        featuredGenreImages[key]?.let { return it }

        val hash = stableGenreHash(key)
        val hue = (hash % 360).toInt()
        val hue2 = (hue + 50 + ((hash ushr 8) % 60).toInt()) % 360
        val hue3 = (hue + 180) % 360
        val seed = URLEncoder.encode(key, "UTF-8")
        val color1 = hueHex(hue, 0.55, 0.42)
        val color2 = hueHex(hue2, 0.60, 0.36)
        val color3 = hueHex(hue3, 0.50, 0.46)
        return "https://api.dicebear.com/9.x/rings/png?seed=$seed&size=256" +
            "&backgroundColor=0b1020,111827&backgroundType=gradientLinear" +
            "&ringColor=$color1,$color2,$color3"
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

    /** 64-bit FNV-1a so unrelated names rarely share the same artwork. */
    private fun stableGenreHash(value: String): Long {
        var hash = -3750763034362895579L
        for (char in value.trim().lowercase()) {
            hash = hash xor char.code.toLong()
            hash *= 1099511628211L
        }
        return hash and Long.MAX_VALUE
    }

    /** Converts HSL to a six-digit lowercase RGB hex string for the artwork service. */
    private fun hueHex(hue: Int, saturation: Double, lightness: Double): String {
        val h = ((hue % 360) + 360) % 360
        val c = (1.0 - kotlin.math.abs(2.0 * lightness - 1.0)) * saturation
        val x = c * (1.0 - kotlin.math.abs((h / 60.0) % 2.0 - 1.0))
        val m = lightness - c / 2.0
        val (r, g, b) = when {
            h < 60 -> Triple(c, x, 0.0)
            h < 120 -> Triple(x, c, 0.0)
            h < 180 -> Triple(0.0, c, x)
            h < 240 -> Triple(0.0, x, c)
            h < 300 -> Triple(x, 0.0, c)
            else -> Triple(c, 0.0, x)
        }
        fun channel(value: Double) = ((value + m) * 255.0).toInt().coerceIn(0, 255)
        return String.format(java.util.Locale.US, "%02x%02x%02x", channel(r), channel(g), channel(b))
    }
}
