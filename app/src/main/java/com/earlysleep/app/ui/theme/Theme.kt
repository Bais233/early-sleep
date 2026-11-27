package com.earlysleep.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB8A6FF),
    onPrimary = Color(0xFF1B1235),
    primaryContainer = Color(0xFF332859),
    onPrimaryContainer = Color(0xFFE9E1FF),
    secondary = Color(0xFF7EE3C4),
    onSecondary = Color(0xFF06281F),
    secondaryContainer = Color(0xFF17453A),
    onSecondaryContainer = Color(0xFFB8F5E3),
    tertiary = Color(0xFFFFA887),
    onTertiary = Color(0xFF3A1608),
    background = Color(0xFF070B1A),
    onBackground = Color(0xFFEDEFFA),
    surface = Color(0xFF11172F),
    onSurface = Color(0xFFEDEFFA),
    surfaceVariant = Color(0xFF181F3D),
    onSurfaceVariant = Color(0xFF9AA3C7),
    outline = Color(0xFF303A63),
    outlineVariant = Color(0xFF252D4E),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF5B4BC4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE5DEFF),
    onPrimaryContainer = Color(0xFF1B1235),
    secondary = Color(0xFF0B7A63),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFB8F5E3),
    onSecondaryContainer = Color(0xFF06281F),
    tertiary = Color(0xFFB9552B),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF5F5FC),
    onBackground = Color(0xFF1B1B2F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1B2F),
    surfaceVariant = Color(0xFFEDECF8),
    onSurfaceVariant = Color(0xFF62617A),
    outline = Color(0xFFC9C6DC),
    outlineVariant = Color(0xFFDDDAEA),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF)
)

@Composable
fun EarlySleepTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content
    )
}
