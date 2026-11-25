package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PicaPressDarkColorScheme = darkColorScheme(
    primary = ProcessCyanGuide,
    onPrimary = ObsidianPasteboard,
    primaryContainer = Color(0xFF073647),
    onPrimaryContainer = Color(0xFFB8F1FF),
    secondary = ProcessMagentaGuide,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A102E),
    onSecondaryContainer = Color(0xFFFFD8E8),
    tertiary = ProcessYellowGold,
    onTertiary = ObsidianPasteboard,
    tertiaryContainer = Color(0xFF423100),
    onTertiaryContainer = Color(0xFFFFE599),
    background = ObsidianPasteboard,
    onBackground = TextPrimaryLight,
    surface = StudioDockSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = StudioCardSurface,
    onSurfaceVariant = TextSecondaryMuted,
    outline = StudioBorderSubtle,
    error = BleedAlertRed,
    onError = Color.White
)

@Composable
fun PicaPressTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PicaPressDarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
