package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// --- Elegant Dark Core Palette ---
val ElegantDarkBg = Color(0xFF0A0C10)       // Main deep black/slate background
val ElegantDarkSurface = Color(0xFF111418)  // Surface and nav footer background
val ElegantAccent = Color(0xFFD1E1FF)       // Signature pale blue/lavender accent
val ElegantAccentDark = Color(0xFF98B7F5)   // Secondary darker elegant blue accent
val ElegantTextMain = Color(0xFFE2E2E6)     // High contrast text
val ElegantTextSecondary = Color(0x99E2E2E6)// Muted text (60%)
val ElegantTextDisabled = Color(0x66E2E2E6) // Muted text (40%)
val ElegantOnline = Color(0xFF4ADE80)       // Beautiful green indicator

// --- Core Palette Fallbacks (keeping old variables to avoid breakage) ---
val PrimarySky = ElegantAccent
val PrimarySkyDark = ElegantAccentDark
val AccentSky = ElegantAccent

// --- Dark Mode Slate ---
val Slate900 = ElegantDarkBg
val Slate800 = ElegantDarkSurface
val Slate700 = Color(0xFF1F242D)
val Slate100 = ElegantTextMain

// --- Light Mode Sky (Elegant Silver/Cream option) ---
val Sky50 = Color(0xFFF4F5F7)
val Sky100 = Color(0xFFE4E6EB)
val Slate800Light = Color(0xFF1C1E21)

// --- Glassmorphic Accents (Liquid Glass) ---
val GlassWhite10 = Color(0x0DFFFFFF) // 5% as requested by design: rgba(255, 255, 255, 0.05)
val GlassWhite20 = Color(0x1AFFFFFF) // 10%
val GlassWhite30 = Color(0x33FFFFFF) // 20%

val GlassBlue10 = Color(0x1AD1E1FF)
val GlassBlue20 = Color(0x33D1E1FF)
val GlassBlue30 = Color(0x4DD1E1FF)
