package com.toxa.pureradio.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.toxa.pureradio.R

/**
 * Geometric display face that matches the "Pure Radio" wordmark in the app icon.
 * Used for headings and titles only; body and label text stay on the system face for
 * maximum legibility on car displays. Subset to Latin + Cyrillic (Hebrew falls back
 * to the system font automatically).
 */
val Montserrat = FontFamily(
    Font(R.font.montserrat_semibold, FontWeight.SemiBold),
    Font(R.font.montserrat_bold, FontWeight.Bold),
)

private val Base = Typography()

private fun TextStyle.display(weight: FontWeight, size: Int, line: Int, tracking: Double = 0.0) = copy(
    fontFamily = Montserrat,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = tracking.sp,
)

val Typography = Typography(
    displayLarge = Base.displayLarge.display(FontWeight.Bold, 52, 60, -0.5),
    displayMedium = Base.displayMedium.display(FontWeight.Bold, 42, 50, -0.25),
    displaySmall = Base.displaySmall.display(FontWeight.Bold, 34, 42),
    headlineLarge = Base.headlineLarge.display(FontWeight.Bold, 30, 38),
    headlineMedium = Base.headlineMedium.display(FontWeight.Bold, 26, 34),
    headlineSmall = Base.headlineSmall.display(FontWeight.Bold, 22, 30),
    titleLarge = Base.titleLarge.display(FontWeight.SemiBold, 20, 28),
    titleMedium = Base.titleMedium.display(FontWeight.SemiBold, 16, 22, 0.1),
    titleSmall = Base.titleSmall.display(FontWeight.SemiBold, 14, 20, 0.1),
    bodyLarge = Base.bodyLarge.copy(fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.3.sp),
    bodyMedium = Base.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.2.sp),
    bodySmall = Base.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.3.sp),
    labelLarge = Base.labelLarge.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp),
    labelMedium = Base.labelMedium.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.4.sp),
    labelSmall = Base.labelSmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp),
)
