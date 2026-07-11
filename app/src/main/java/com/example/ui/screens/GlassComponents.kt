package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// --- Theme Wallpapers / Background Gradients ---
object ChatWallpaper {
    val ClassicSlate = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0A0C10),
            Color(0xFF161920),
            Color(0xFF0A0C10)
        )
    )

    val NeonSky = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF050510),
            Color(0xFF101424),
            Color(0xFF1F102B),
            Color(0xFF050510)
        )
    )

    val NeonSunset = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF140505),
            Color(0xFF330707),
            Color(0xFF2C1045),
            Color(0xFF0A0514)
        )
    )

    val MagicForest = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF01140F),
            Color(0xFF033024),
            Color(0xFF0A0C10)
        )
    )

    fun getBrush(wallpaperValue: String): Brush {
        return when (wallpaperValue) {
            "bg_neon" -> NeonSky
            "bg_sunset" -> NeonSunset
            "bg_forest" -> MagicForest
            else -> ClassicSlate
        }
    }
}

// --- Glassmorphic Container ---
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    borderWidth: Dp = 1.dp,
    alpha: Float = 0.05f, // As requested by Elegant Dark: background: rgba(255, 255, 255, 0.05)
    content: @Composable () -> Unit
) {
    val isLight = androidx.compose.material3.MaterialTheme.colorScheme.background != Color(0xFF0A0C10)
    Surface(
        modifier = modifier,
        color = if (isLight) Color.White else Color.White.copy(alpha = alpha),
        shape = RoundedCornerShape(cornerRadius),
        border = BorderStroke(borderWidth, if (isLight) Color.LightGray.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f)), // border: 1px solid rgba(255, 255, 255, 0.1)
        shadowElevation = if (isLight) 2.dp else 0.dp,
        tonalElevation = 0.dp
    ) {
        content()
    }
}

@Composable
fun DarkGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    borderWidth: Dp = 1.dp,
    alpha: Float = 0.45f,
    content: @Composable () -> Unit
) {
    val isLight = androidx.compose.material3.MaterialTheme.colorScheme.background != Color(0xFF0A0C10)
    Surface(
        modifier = modifier,
        color = if (isLight) Color(0xFFF1F5F9) else Color(0xFF0A0C10).copy(alpha = alpha),
        shape = RoundedCornerShape(cornerRadius),
        border = BorderStroke(borderWidth, if (isLight) Color.LightGray.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f)),
        shadowElevation = if (isLight) 1.dp else 0.dp,
        tonalElevation = 0.dp
    ) {
        content()
    }
}
