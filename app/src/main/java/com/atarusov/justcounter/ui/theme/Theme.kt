package com.atarusov.justcounter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = darkGrey,
    onSurfaceVariant = white,
    surface = extraDarkGrey,
    onSurface = white,
    primaryContainer = lightOrange,
    onPrimaryContainer = extraDarkGrey,
    surfaceContainerHighest = darkGrey,
)

private val LightColorScheme = lightColorScheme(
    background = white,
    surface = white,
    onSurface = black,
    primaryContainer = lightOrange,
    onPrimaryContainer = extraDarkGrey,
    surfaceContainerHighest = lightGrey
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