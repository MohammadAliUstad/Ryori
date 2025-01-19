@file:Suppress("DEPRECATION")

package com.yugentech.ryori.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

fun blackColorScheme(): ColorScheme = ColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    primaryContainer = Color.Black,
    onPrimaryContainer = Color.White,
    inversePrimary = Color.White,
    secondary = Color.Gray,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF424242),
    onSecondaryContainer = Color.White,
    tertiary = Color.White, // Teal
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF121212),
    onTertiaryContainer = Color.White,
    background = Color.Black,
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF121212),
    onSurface = Color.White,
    surfaceVariant = Color.DarkGray,
    onSurfaceVariant = Color(0xFFE0E0E0),
    surfaceTint = Color.Black,
    inverseSurface = Color.White,
    inverseOnSurface = Color.Black,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    errorContainer = Color.Red,
    onErrorContainer = Color.White,
    outline = Color.Gray,
    outlineVariant = Color(0xFF666666),
    scrim = Color.Black
)

fun whiteColorScheme(): ColorScheme = ColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    primaryContainer = Color.White,
    onPrimaryContainer = Color.Black,
    inversePrimary = Color.Black,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFE0E0E0),
    onSecondaryContainer = Color.Black,
    tertiary = Color.Black,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFBB86FC),
    onTertiaryContainer = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color.Black,
    surfaceTint = Color.White,
    inverseSurface = Color.Black,
    inverseOnSurface = Color.White,
    error = Color(0xFFB00020),
    onError = Color.White,
    errorContainer = Color(0xFFCF6679),
    onErrorContainer = Color.Black,
    outline = Color.Gray,
    outlineVariant = Color(0xFFBDBDBD),
    scrim = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color.DarkGray,
    onPrimary = Color.Black,
    primaryContainer = Color.DarkGray,
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    tertiary = Color(0xFFCF6679),
    onTertiary = Color.White,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1F1F1F),
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color.DarkGray,
    onPrimary = Color.White,
    primaryContainer = Color.White,
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFF03DAC5),
    onSecondary = Color.Black,
    tertiary = Color(0xFFB00020),
    onTertiary = Color.White,
    background = Color(0xFFFFFFFF),
    onBackground = Color.Black,
    surface = Color(0xFFFFFFFF),
    onSurface = Color.Black
)

@Composable
fun RyoriTheme(
    selectedTheme: String, content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when (selectedTheme) {
        "Black" -> blackColorScheme()
        "White" -> whiteColorScheme()
        "Dark" -> dynamicDarkColorScheme(context)
        "Light" -> dynamicLightColorScheme(context)
        "System" -> {
            if (isSystemInDarkTheme()) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        else -> {
            if (isSystemInDarkTheme()) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}