package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF80CBC4), // Vibrant Teal Accent
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF134E4A),
    onPrimaryContainer = Color(0xFFCCFBF1),
    secondary = Color(0xFF5EEAD4),
    onSecondary = Color(0xFF0F172A),
    background = Color(0xFF0F172A), // Deep Slate Navy
    surface = Color(0xFF1E293B), // Rich Card Surface
    surfaceVariant = Color(0xFF334155), // Card Outline/Divider
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF94A3B8)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTeal,
    onPrimary = Color.White,
    primaryContainer = LightTealContainer,
    onPrimaryContainer = PrimaryTealDark,
    secondary = SecondaryTeal,
    onSecondary = Color.White,
    background = BackgroundWarm,
    surface = SurfaceWarm,
    surfaceVariant = SurfaceVariantWarm,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryMuted
)

@Composable
fun AnticoagulantTheme(
    themeMode: String = "SYSTEM", // "LIGHT", "DARK", "SYSTEM"
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "LIGHT" -> false
        "DARK" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
