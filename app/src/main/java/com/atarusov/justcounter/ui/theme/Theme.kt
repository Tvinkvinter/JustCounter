package com.atarusov.justcounter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = darkGrey,
    surface = extraDarkGrey,
    onSurfaceVariant = grey,
    onSurface = white,
    primaryContainer = lightOrange,
    onPrimaryContainer = extraDarkGrey,
    onTertiary = white,
    surfaceContainerHighest = darkGrey,
    inverseOnSurface = extraDarkGrey
)

private val LightColorScheme = lightColorScheme(
    background = white,
    surface = white,
    onSurface = black,
    onSurfaceVariant = grey,
    primaryContainer = lightOrange,
    onPrimaryContainer = extraDarkGrey,
    onTertiary = white,
    surfaceContainerHighest = lightGrey,
    inverseOnSurface = white
)

@Composable
fun JustCounterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}