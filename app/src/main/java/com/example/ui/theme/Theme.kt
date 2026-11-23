package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DormCleanColorScheme = lightColorScheme(
    primary = MaroonPrimary,
    onPrimary = Color.White,
    primaryContainer = MaroonTint,
    onPrimaryContainer = MaroonPrimary,
    secondary = MaroonDeep,
    onSecondary = Color.White,
    secondaryContainer = BlushPinkCard,
    onSecondaryContainer = MaroonPrimary,
    tertiary = GCashBlue,
    onTertiary = Color.White,
    background = WarmCreamBg,
    onBackground = InkPrimary,
    surface = CardWhite,
    onSurface = InkPrimary,
    surfaceVariant = PillTrackBg,
    onSurfaceVariant = InkSecondary,
    outline = CardSubtleBorder,
    outlineVariant = SoftInputBorder,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun DormCleanTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DormCleanColorScheme,
        typography = Typography,
        content = content
    )
}
