package com.app.gimnasio.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GymDarkColorScheme = darkColorScheme(
    primary = LimeGreen,
    onPrimary = Color.Black,
    primaryContainer = DarkCard,
    onPrimaryContainer = TextWhite,
    secondary = LimeGreenDark,
    onSecondary = Color.Black,
    background = DarkBackground,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextGray,
    outline = DarkBorder
)

@Composable
fun GimnasioTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GymDarkColorScheme,
        typography = Typography,
        content = content
    )
}
