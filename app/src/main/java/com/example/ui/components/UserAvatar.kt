package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun UserAvatar(
    avatarUrl: String?,
    name: String,
    size: Dp,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp)
) {
    if (!avatarUrl.isNullOrBlank()) {
        Image(
            painter = rememberAsyncImagePainter(model = avatarUrl),
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(shape)
        )
    } else {
        // Extract initials (up to 2 characters)
        val cleanName = name.trim()
        val initials = if (cleanName.isNotEmpty()) {
            val parts = cleanName.split("\\s+".toRegex()).filter { it.isNotBlank() }
            if (parts.size >= 2) {
                "${parts[0].firstOrNull() ?: ""}${parts[1].firstOrNull() ?: ""}"
            } else {
                "${cleanName.firstOrNull() ?: ""}"
            }
        } else {
            "?"
        }.uppercase()

        // Aesthetic, energetic gradient color schemes (Telegram style)
        val colors = listOf(
            listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), // Blue
            listOf(Color(0xFF10B981), Color(0xFF047857)), // Green
            listOf(Color(0xFFEF4444), Color(0xFFB91C1C)), // Red
            listOf(Color(0xFFF59E0B), Color(0xFFD97706)), // Orange/Amber
            listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)), // Purple
            listOf(Color(0xFFEC4899), Color(0xFFBE185D)), // Pink
            listOf(Color(0xFF06B6D4), Color(0xFF0891B2)), // Cyan
            listOf(Color(0xFF14B8A6), Color(0xFF0F766E))  // Teal
        )
        
        val index = if (name.isNotEmpty()) {
            Math.abs(name.hashCode()) % colors.size
        } else {
            0
        }
        val gradient = Brush.linearGradient(colors[index])

        Box(
            modifier = modifier
                .size(size)
                .clip(shape)
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = Color.White,
                fontSize = (size.value * 0.38f).sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
