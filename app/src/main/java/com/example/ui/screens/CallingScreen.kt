package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.ui.theme.PrimarySky
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.ChatViewModel
import com.example.ui.viewmodel.Trans
import com.example.ui.components.UserAvatar

@Composable
fun CallingScreen(viewModel: ChatViewModel) {
    val activeCall by viewModel.activeCall.collectAsState()
    val settings by viewModel.settings.collectAsState()

    val call = activeCall ?: return
    val contact = call.contact

    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(false) }

    // Pulsating animation for the avatar ring
    val infiniteTransition = rememberInfiniteTransition(label = "pulsatingRing")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0C10),
                        Color(0xFF161920),
                        Color(0xFF0A0C10)
                    )
                )
            )
            .systemBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            // Top section: Call Type Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Text(
                    text = Trans.get(settings.language, "voice_call"),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.4f),
                        letterSpacing = 2.sp
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color(0xFFD1E1FF),
                        fontSize = 28.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                val statusText = when (call.status) {
                    "DIALING" -> Trans.get(settings.language, "calling")
                    "CONNECTED" -> "${Trans.get(settings.language, "connected")} ${viewModel.formatDuration(call.duration)}"
                    "DISCONNECTED" -> Trans.get(settings.language, "call_ended")
                    else -> ""
                }
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (call.status == "CONNECTED") Color(0xFF4ADE80) else Color.White.copy(alpha = 0.6f)
                    )
                )
            }

            // Middle section: Glowing Pulsating Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {
                // Pulsating Glowing ring
                if (call.status == "CONNECTED" || call.status == "DIALING") {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(Color(0xFFD1E1FF).copy(alpha = 0.08f))
                            .border(2.dp, Color(0xFFD1E1FF).copy(alpha = 0.2f), CircleShape)
                    )
                }

                // Main Avatar with thin elegant border
                UserAvatar(
                    avatarUrl = contact.avatarUrl,
                    name = contact.name,
                    size = 140.dp,
                    shape = CircleShape,
                    modifier = Modifier.border(3.dp, Color(0xFFD1E1FF), CircleShape)
                )
            }

            // Bottom section: Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                // Simulated sound waves when talking
                if (call.status == "CONNECTED") {
                    Row(
                        modifier = Modifier
                            .padding(bottom = 32.dp)
                            .height(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { index ->
                            val heightPulse by infiniteTransition.animateFloat(
                                initialValue = 4f,
                                targetValue = 24f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(
                                        durationMillis = 300 + (index * 100),
                                        easing = LinearEasing
                                    ),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "wavePulse"
                            )
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(heightPulse.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Button
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (isMuted) Color.White else Color.White.copy(alpha = 0.15f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = if (isMuted) Slate900 else Color.White
                        )
                    }

                    // Hang up Button (Red)
                    IconButton(
                        onClick = { viewModel.endCall() },
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFFEF4444), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "Hang up",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Speaker Button
                    IconButton(
                        onClick = { isSpeakerOn = !isSpeakerOn },
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (isSpeakerOn) Color.White else Color.White.copy(alpha = 0.15f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speaker",
                            tint = if (isSpeakerOn) Slate900 else Color.White
                        )
                    }
                }
            }
        }
    }
}
