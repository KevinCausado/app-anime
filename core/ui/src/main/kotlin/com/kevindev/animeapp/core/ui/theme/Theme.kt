package com.kevindev.animeapp.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AnimeDarkColorScheme = darkColorScheme(
    primary = AnimeOrange,
    onPrimary = AnimeOnBg,
    primaryContainer = AnimeOrangeDark,
    onPrimaryContainer = AnimeOnBg,
    secondary = AnimeSurfaceVariant,
    onSecondary = AnimeOnBg,
    background = AnimeBg,
    onBackground = AnimeOnBg,
    surface = AnimeSurface,
    onSurface = AnimeOnSurface,
    surfaceVariant = AnimeSurfaceVariant,
    onSurfaceVariant = AnimeOnSurfaceMuted,
    error = AnimeError,
    onError = AnimeOnBg,
)

@Composable
fun AnimeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AnimeDarkColorScheme,
        typography = AnimeTypography,
        content = content,
    )
}
