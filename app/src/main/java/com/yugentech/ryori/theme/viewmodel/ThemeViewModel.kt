package com.yugentech.ryori.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.ryori.data.settings.SettingsRepository
import com.yugentech.ryori.theme.AppFont
import com.yugentech.ryori.theme.config.ThemeConfiguration
import com.yugentech.ryori.theme.repository.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val repository: ThemeRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    // The "Haptic feedback" switch on the More screen; RyoriTheme gates all haptics on it.
    val hapticsEnabled: StateFlow<Boolean> = settingsRepository.settings
        .map { it.hapticsEnabled }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    // Internal mutable state initialized with the repository's current configuration (as in
    // Sessions), so a new instance, such as the Appearance screen's, starts on the saved theme
    private val _themeConfiguration = MutableStateFlow(repository.themeConfiguration.value)

    // Public read-only stream of theme configuration
    val themeConfiguration: StateFlow<ThemeConfiguration> = _themeConfiguration.asStateFlow()

    // False until the saved theme has been read once. MainActivity keeps the splash screen up
    // until then, so the app never draws with the default theme first.
    val isLoaded: StateFlow<Boolean> = repository.isLoaded

    // Derived stream specifically for the current font, helpful for UI logic
    val currentFont: StateFlow<AppFont> = _themeConfiguration
        .map { it.appFont }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _themeConfiguration.value.appFont
        )

    init {
        // Automatically update local state whenever the repository data changes
        viewModelScope.launch {
            repository.themeConfiguration.collect { config ->
                _themeConfiguration.value = config
            }
        }
    }

    // Updates the full theme configuration and persists it
    fun updateTheme(config: ThemeConfiguration) {
        _themeConfiguration.value = config

        viewModelScope.launch {
            repository.setThemeConfig(config)
        }
    }

    // Helper method to update just the font while keeping other settings
    fun setFont(font: AppFont) {
        val current = _themeConfiguration.value
        val newConfig = current.copy(appFont = font)
        updateTheme(newConfig)
    }
}
