package com.yugentech.ryori.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.ryori.api.cache.CachedApi
import com.yugentech.ryori.data.settings.SettingsRepository
import com.yugentech.ryori.data.settings.UserSettings
import com.yugentech.ryori.data.stats.KitchenRepository
import com.yugentech.ryori.data.stats.KitchenStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MoreUiState(
    val settings: UserSettings = UserSettings(),
    val stats: KitchenStats = KitchenStats(),
    val recentCount: Int = 0,
    val cacheSizeBytes: Long = 0
)

// Backs the More tab: the chef profile, kitchen stats and every preference switch.
class MoreViewModel(
    private val settingsRepository: SettingsRepository,
    private val kitchen: KitchenRepository,
    private val api: CachedApi
) : ViewModel() {

    val uiState: StateFlow<MoreUiState> = combine(
        settingsRepository.settings,
        kitchen.stats,
        kitchen.recentCount,
        api.sizeBytes
    ) { settings, stats, recentCount, cacheSize ->
        MoreUiState(
            settings = settings,
            stats = stats,
            recentCount = recentCount,
            cacheSizeBytes = cacheSize
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MoreUiState())

    fun setChefName(name: String) = persist { settingsRepository.setChefName(name) }

    fun setVegetarianMode(enabled: Boolean) = persist { settingsRepository.setVegetarianMode(enabled) }

    fun setKeepScreenOn(enabled: Boolean) = persist { settingsRepository.setKeepScreenOn(enabled) }

    fun setHaptics(enabled: Boolean) = persist { settingsRepository.setHaptics(enabled) }

    fun clearCache() = persist { api.clear() }

    fun clearRecent() = persist { kitchen.clearRecent() }

    private fun persist(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}
