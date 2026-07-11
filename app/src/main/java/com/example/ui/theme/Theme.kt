package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElegantAccent,
    secondary = ElegantAccentDark,
    tertiary = Color(0xFF1F242D),
    background = ElegantDarkBg,
    surface = ElegantDarkSurface,
    onPrimary = ElegantDarkBg,
    onSecondary = ElegantDarkBg,
    onTertiary = ElegantTextMain,
    onBackground = ElegantTextMain,
    onSurface = ElegantTextMain
)

private val LightColorScheme = lightColorScheme(
    primary = ElegantAccentDark,
    secondary = ElegantAccent,
    tertiary = Sky100,
    background = Sky50,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Slate800Light,
    onTertiary = Slate800Light,
    onBackground = Slate800Light,
    onSurface = Slate800Light
)

@Composable
fun MyApplicationTheme(
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
