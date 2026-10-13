package com.toxa.pureradio.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.toxa.pureradio.ui.viewmodel.AppTheme
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.darkColorScheme as tvDarkColorScheme

fun paletteFor(theme: AppTheme): PurePalette = when (theme) {
    AppTheme.BlueNeon -> BlueNeonPalette
    AppTheme.Violet -> VioletPalette
    AppTheme.Monochrome -> MonochromePalette
    AppTheme.Forest -> ForestPalette
    AppTheme.Contrast -> ContrastPalette
    AppTheme.Black -> BlackPalette
}

private fun PurePalette.toColorScheme(): ColorScheme = darkColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    inversePrimary = primaryContainer,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = primaryContainer,
    onTertiaryContainer = onPrimaryContainer,
    background = background,
    onBackground = onSurface,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    surfaceTint = primary,
    inverseSurface = onSurface,
    inverseOnSurface = surface,
    error = ErrorTone,
    onError = OnErrorTone,
    errorContainer = ErrorContainerTone,
    onErrorContainer = OnErrorContainerTone,
    outline = outline,
    outlineVariant = outlineVariant,
    scrim = Color.Black,
    surfaceBright = surfaceHighest,
    surfaceDim = background,
    surfaceContainerLowest = surfaceLowest,
    surfaceContainerLow = surfaceLow,
    surfaceContainer = surfaceContainer,
    surfaceContainerHigh = surfaceHigh,
    surfaceContainerHighest = surfaceHighest,
)

/** Same palette for the androidx.tv Material theme, so TV screens follow the selected app theme too. */
@OptIn(ExperimentalTvMaterial3Api::class)
private fun PurePalette.toTvColorScheme() = tvDarkColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    tertiary = tertiary,
    onTertiary = onTertiary,
    background = background,
    onBackground = onSurface,
    surface = surfaceContainer,
    onSurface = onSurface,
    surfaceVariant = surfaceHigh,
    onSurfaceVariant = onSurfaceVariant,
    error = ErrorTone,
    onError = OnErrorTone,
    border = outline,
    borderVariant = outlineVariant,
)

val PureShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PureRadioTheme(
    theme: AppTheme = AppTheme.BlueNeon,
    content: @Composable () -> Unit,
) {
    val palette = paletteFor(theme)
    val colorScheme = remember(theme) { palette.toColorScheme() }
    val tvColorScheme = remember(theme) { palette.toTvColorScheme() }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = PureShapes,
    ) {
        TvMaterialTheme(colorScheme = tvColorScheme, content = content)
    }
}
