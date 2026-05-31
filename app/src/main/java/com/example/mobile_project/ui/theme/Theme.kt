package com.example.mobile_project.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = MinLishPrimaryLight,
    onPrimary = Color.White,
    secondary = MinLishSecondary,
    tertiary = MinLishTertiary,
    background = MinLishTextPrimary,
    surface = Color(0xFF18384F),
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = MinLishPrimary,
    onPrimary = Color.White,
    primaryContainer = MinLishPrimaryContainer,
    onPrimaryContainer = MinLishTextPrimary,
    secondary = MinLishSecondary,
    secondaryContainer = MinLishSecondaryContainer,
    tertiary = MinLishTertiary,
    tertiaryContainer = MinLishTertiaryContainer,
    background = MinLishBackground,
    onBackground = MinLishTextPrimary,
    surface = MinLishSurface,
    onSurface = MinLishTextPrimary,
    surfaceContainer = MinLishSurfaceContainer,
    outline = MinLishOutline,
    error = MinLishError,
    errorContainer = MinLishErrorContainer
)

private val MinLishShapes = Shapes(
    extraSmall = RoundedCornerShape(14.dp),
    small = RoundedCornerShape(18.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp),
    extraLarge = RoundedCornerShape(40.dp)
)

@Composable
fun Mobile_projectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = MinLishShapes,
        content = content
    )
}
