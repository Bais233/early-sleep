package com.earlysleep.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NightColorScheme = darkColorScheme(
    primary = Lavender,
    onPrimary = Color(0xFF1B1235),
    primaryContainer = Color(0xFF332859),
    onPrimaryContainer = Color(0xFFE9E1FF),
    secondary = Mint,
    onSecondary = Color(0xFF06281F),
    secondaryContainer = Color(0xFF17453A),
    onSecondaryContainer = Color(0xFFB8F5E3),
    tertiary = WarningOrange,
    onTertiary = Color(0xFF3A1608),
    background = NightBackground,
    onBackground = TextPrimary,
    surface = NightSurface,
    onSurface = TextPrimary,
    surfaceVariant = NightSurfaceHigh,
    onSurfaceVariant = TextSecondary,
    outline = OutlineNight,
    outlineVariant = OutlineNight,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun EarlySleepTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NightColorScheme,
        content = content
    )
}
