package com.yugentech.ryori.theme.repository

import com.yugentech.ryori.theme.config.ThemeConfiguration
import com.yugentech.ryori.theme.datastore.ThemeDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

// Same setup as Sessions: one app-wide holder of the theme, read from disk once at start-up and
// kept current from then on. Every ThemeViewModel starts from its value, so a screen opened
// later (Appearance) already has the saved theme on its first frame instead of animating to it.
class ThemeRepositoryImpl(
    private val dataStore: ThemeDataStore,
    externalScope: CoroutineScope
) : ThemeRepository {

    private val _themeConfiguration = MutableStateFlow(ThemeConfiguration())

    // Hot flow keeping the latest theme configuration immediately available
    override val themeConfiguration: StateFlow<ThemeConfiguration> =
        _themeConfiguration.asStateFlow()

    private val _isLoaded = MutableStateFlow(false)
    override val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    init {
        externalScope.launch {
            dataStore.themeConfiguration.collect { config ->
                _themeConfiguration.value = config
                _isLoaded.value = true
            }
        }
    }

    override suspend fun setThemeConfig(config: ThemeConfiguration) {
        Timber.i("Updating theme configuration: Mode=${config.themeMode}, Color=${config.colorTheme}")
        _themeConfiguration.value = config
        // Pass the new configuration to the service for persistence
        dataStore.updateThemeConfig(config)
    }

    override suspend fun resetThemeToDefaults() {
        Timber.d("Resetting theme to defaults")
        // Trigger the service to clear custom settings
        dataStore.resetToDefaults()
    }
}
