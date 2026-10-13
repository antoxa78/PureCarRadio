package com.toxa.pureradio.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * A complete dark palette for one app theme.
 *
 * Every Material 3 colour role is derived from this, so no component falls back to the
 * baseline (purple-tinted) defaults. Neutrals are slightly tinted toward each theme's accent
 * so surfaces, dividers and secondary text feel like they belong to the same family.
 */
data class PurePalette(
    // Accent
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    // Neutrals, darkest to lightest
    val background: Color,
    val surfaceLowest: Color,
    val surface: Color,
    val surfaceLow: Color,
    val surfaceContainer: Color,
    val surfaceHigh: Color,
    val surfaceHighest: Color,
    val surfaceVariant: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val outlineVariant: Color,
)

// Shared error tones (readable on every dark palette)
val ErrorTone = Color(0xFFFF8A80)
val OnErrorTone = Color(0xFF3B0A0A)
val ErrorContainerTone = Color(0xFF5C1717)
val OnErrorContainerTone = Color(0xFFFFDAD6)

/** Brand palette: electric blue on deep navy, matching the app icon and splash screen. */
val BlueNeonPalette = PurePalette(
    primary = Color(0xFF3D8BFF),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF0E2A5C),
    onPrimaryContainer = Color(0xFFBBD4FF),
    secondary = Color(0xFF8FB6FF),
    onSecondary = Color(0xFF04122E),
    secondaryContainer = Color(0xFF1A3566),
    onSecondaryContainer = Color(0xFFD6E4FF),
    tertiary = Color(0xFF6FE3FF),
    onTertiary = Color(0xFF00202A),
    background = Color(0xFF060912),
    surfaceLowest = Color(0xFF04070F),
    surface = Color(0xFF0A0E1A),
    surfaceLow = Color(0xFF0D1220),
    surfaceContainer = Color(0xFF111729),
    surfaceHigh = Color(0xFF172036),
    surfaceHighest = Color(0xFF1E2942),
    surfaceVariant = Color(0xFF1A2236),
    onSurface = Color(0xFFE8ECF6),
    onSurfaceVariant = Color(0xFFA3ADC4),
    outline = Color(0xFF5B6684),
    outlineVariant = Color(0xFF262F47),
)

val VioletPalette = PurePalette(
    primary = Color(0xFFBB86FC),
    onPrimary = Color(0xFF1E0838),
    primaryContainer = Color(0xFF3A1E63),
    onPrimaryContainer = Color(0xFFE9D7FF),
    secondary = Color(0xFFD0B3FF),
    onSecondary = Color(0xFF250B45),
    secondaryContainer = Color(0xFF34224F),
    onSecondaryContainer = Color(0xFFEBDDFF),
    tertiary = Color(0xFFF0A8E0),
    onTertiary = Color(0xFF3A0F33),
    background = Color(0xFF0B0812),
    surfaceLowest = Color(0xFF07050C),
    surface = Color(0xFF100C18),
    surfaceLow = Color(0xFF150F1F),
    surfaceContainer = Color(0xFF1A1426),
    surfaceHigh = Color(0xFF221A31),
    surfaceHighest = Color(0xFF2B213D),
    surfaceVariant = Color(0xFF241C33),
    onSurface = Color(0xFFEEE8F7),
    onSurfaceVariant = Color(0xFFB6AAC8),
    outline = Color(0xFF6C6080),
    outlineVariant = Color(0xFF2F2640),
)

val MonochromePalette = PurePalette(
    primary = Color(0xFFC8C8CC),
    onPrimary = Color(0xFF111112),
    primaryContainer = Color(0xFF3A3A3E),
    onPrimaryContainer = Color(0xFFEDEDED),
    secondary = Color(0xFFA8A8AC),
    onSecondary = Color(0xFF111112),
    secondaryContainer = Color(0xFF38383C),
    onSecondaryContainer = Color(0xFFEDEDED),
    tertiary = Color(0xFFE0E0E0),
    onTertiary = Color(0xFF111112),
    background = Color(0xFF0E0E0F),
    surfaceLowest = Color(0xFF09090A),
    surface = Color(0xFF131314),
    surfaceLow = Color(0xFF18181A),
    surfaceContainer = Color(0xFF1D1D1F),
    surfaceHigh = Color(0xFF252527),
    surfaceHighest = Color(0xFF2E2E31),
    surfaceVariant = Color(0xFF2A2A2C),
    onSurface = Color(0xFFEDEDED),
    onSurfaceVariant = Color(0xFFB0B0B3),
    outline = Color(0xFF6B6B70),
    outlineVariant = Color(0xFF333336),
)

val ForestPalette = PurePalette(
    primary = Color(0xFF5CC968),
    onPrimary = Color(0xFF03210A),
    primaryContainer = Color(0xFF134A1E),
    onPrimaryContainer = Color(0xFFB8F2BE),
    secondary = Color(0xFF8DD694),
    onSecondary = Color(0xFF05250C),
    secondaryContainer = Color(0xFF1D3A23),
    onSecondaryContainer = Color(0xFFCDEFD1),
    tertiary = Color(0xFFA5E3A9),
    onTertiary = Color(0xFF05250C),
    background = Color(0xFF070C08),
    surfaceLowest = Color(0xFF040805),
    surface = Color(0xFF0B110C),
    surfaceLow = Color(0xFF0F1711),
    surfaceContainer = Color(0xFF131C15),
    surfaceHigh = Color(0xFF1A251C),
    surfaceHighest = Color(0xFF213024),
    surfaceVariant = Color(0xFF1C281F),
    onSurface = Color(0xFFE6EFE7),
    onSurfaceVariant = Color(0xFFA6B8A9),
    outline = Color(0xFF5E7362),
    outlineVariant = Color(0xFF26342A),
)

/** Maximum legibility: pure white accents, stark selection states. */
val ContrastPalette = PurePalette(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF2E2E2E),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFFE0E0E0),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF000000),
    tertiary = Color(0xFFBDBDBD),
    onTertiary = Color(0xFF000000),
    background = Color(0xFF000000),
    surfaceLowest = Color(0xFF000000),
    surface = Color(0xFF0A0A0A),
    surfaceLow = Color(0xFF0F0F0F),
    surfaceContainer = Color(0xFF141414),
    surfaceHigh = Color(0xFF1C1C1C),
    surfaceHighest = Color(0xFF262626),
    surfaceVariant = Color(0xFF222222),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFD6D6D6),
    outline = Color(0xFF9E9E9E),
    outlineVariant = Color(0xFF4A4A4A),
)

/** True-black OLED palette. */
val BlackPalette = PurePalette(
    primary = Color(0xFFF2F2F2),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF1A1A1A),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFFCCCCCC),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF262626),
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = Color(0xFFAAAAAA),
    onTertiary = Color(0xFF000000),
    background = Color(0xFF000000),
    surfaceLowest = Color(0xFF000000),
    surface = Color(0xFF000000),
    surfaceLow = Color(0xFF070707),
    surfaceContainer = Color(0xFF0C0C0C),
    surfaceHigh = Color(0xFF141414),
    surfaceHighest = Color(0xFF1C1C1C),
    surfaceVariant = Color(0xFF121212),
    onSurface = Color(0xFFF2F2F2),
    onSurfaceVariant = Color(0xFFA8A8A8),
    outline = Color(0xFF5A5A5A),
    outlineVariant = Color(0xFF222222),
)
