package com.yugentech.ryori.ui.config.appearanceScreen.components

import androidx.compose.ui.graphics.Color
import com.yugentech.ryori.theme.AppColorSchemes
import com.yugentech.ryori.theme.config.ColorTheme

data class ThemeOption(
    val colorTheme: ColorTheme,
    val displayName: String,
    val primaryColor: Color,
    val gradientColors: List<Color>
)

fun themeOptions(
    currentPrimary: Color,
    currentPrimaryContainer: Color
): List<ThemeOption> = listOf(

    // Dynamic
    ThemeOption(
        colorTheme = ColorTheme.DYNAMIC,
        displayName = "Dynamic",
        primaryColor = currentPrimary,
        gradientColors = listOf(
            currentPrimary,
            currentPrimaryContainer
        )
    ),

    // Ryori (Brown/Orange): the app's own theme and the default, right after Dynamic. Stored as
    // CANYON, its old name, so a saved choice carries over.
    ThemeOption(
        colorTheme = ColorTheme.CANYON,
        displayName = "Ryori",
        primaryColor = AppColorSchemes.CanyonLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.CanyonLightColorScheme.primary,
            AppColorSchemes.CanyonLightColorScheme.primaryContainer
        )
    ),

    // Lagoon (Cyan/Teal)
    ThemeOption(
        colorTheme = ColorTheme.LAGOON,
        displayName = "Lagoon",
        primaryColor = AppColorSchemes.LagoonLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.LagoonLightColorScheme.primary,
            AppColorSchemes.LagoonLightColorScheme.primaryContainer
        )
    ),

    // Grove (Olive/Yellow-Green)
    ThemeOption(
        colorTheme = ColorTheme.GROVE,
        displayName = "Grove",
        primaryColor = AppColorSchemes.GroveLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.GroveLightColorScheme.primary,
            AppColorSchemes.GroveLightColorScheme.primaryContainer
        )
    ),

    // Harvest (Gold/Yellow)
    ThemeOption(
        colorTheme = ColorTheme.HARVEST,
        displayName = "Harvest",
        primaryColor = AppColorSchemes.HarvestLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.HarvestLightColorScheme.primary,
            AppColorSchemes.HarvestLightColorScheme.primaryContainer
        )
    ),

    // Sakura (Pink/Red)
    ThemeOption(
        colorTheme = ColorTheme.SAKURA,
        displayName = "Sakura",
        primaryColor = AppColorSchemes.SakuraLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.SakuraLightColorScheme.primary,
            AppColorSchemes.SakuraLightColorScheme.primaryContainer
        )
    ),

    // Alpine (Deep Green/Forest)
    ThemeOption(
        colorTheme = ColorTheme.ALPINE,
        displayName = "Alpine",
        primaryColor = AppColorSchemes.AlpineLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.AlpineLightColorScheme.primary,
            AppColorSchemes.AlpineLightColorScheme.primaryContainer
        )
    ),

    // Twilight (Indigo/Purple tones)
    ThemeOption(
        colorTheme = ColorTheme.TWILIGHT,
        displayName = "Twilight",
        primaryColor = AppColorSchemes.TwilightLightColorScheme.primary,
        gradientColors = listOf(
            AppColorSchemes.TwilightLightColorScheme.primary,
            AppColorSchemes.TwilightLightColorScheme.primaryContainer
        )
    )
)
