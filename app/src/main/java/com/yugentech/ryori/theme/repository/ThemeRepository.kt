package com.yugentech.ryori.theme.repository

import com.yugentech.ryori.theme.config.ThemeConfiguration
import kotlinx.coroutines.flow.StateFlow

interface ThemeRepository {

    // Provides a real-time stream of the current theme settings, its latest value always at hand
    val themeConfiguration: StateFlow<ThemeConfiguration>

    // False until the saved theme has been read from disk once
    val isLoaded: StateFlow<Boolean>

    // Updates the current theme configuration with new values
    suspend fun setThemeConfig(config: ThemeConfiguration)

    // Reverts all theme settings back to the factory defaults
    suspend fun resetThemeToDefaults()
}
