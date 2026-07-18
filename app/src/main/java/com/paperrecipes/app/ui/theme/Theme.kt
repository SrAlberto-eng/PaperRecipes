package com.paperrecipes.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Clay,
    onPrimary = Color.White,
    primaryContainer = ClaySoft,
    onPrimaryContainer = InkPrimary,
    secondary = InkSecondary,
    onSecondary = PaperSurface,
    background = PaperBg,
    onBackground = InkPrimary,
    surface = PaperSurface,
    onSurface = InkPrimary,
    surfaceVariant = PaperSurfaceAlt,
    onSurfaceVariant = InkSecondary,
    outline = PaperBorder,
    outlineVariant = PaperBorder,
    error = DangerRed,
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = ClayDark,
    onPrimary = CharcoalBackground,
    primaryContainer = Clay,
    onPrimaryContainer = PaperText,
    secondary = PaperTextMuted,
    onSecondary = CharcoalBackground,
    onBackground = PaperText,
    surface = CharcoalSurface,
    onSurface = PaperText,
    surfaceVariant = CharcoalSurfaceAlt,
    onSurfaceVariant = PaperTextMuted,
    outline = CharcoalBorder,
    outlineVariant = CharcoalBorder,
    error = DangerRed,
    onError = Color.White,
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun PaperRecipesTheme(
    //darkTheme: Boolean = isSystemInDarkTheme(),
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insets = WindowCompat.getInsetsController(window, view)
            insets.isAppearanceLightStatusBars = !darkTheme
            insets.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}