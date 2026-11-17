package com.yugentech.ryori.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.ryori.data.settings.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Whether to open on the first-run intro. Null until the saved flag is read, so the splash can
// wait for it and the app never flashes Main before switching to the intro.
class OnboardingViewModel(private val settings: SettingsRepository) : ViewModel() {

    val showOnboarding: StateFlow<Boolean?> = settings.onboardingCompleted
        .map { done -> !done }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun complete() {
        viewModelScope.launch { settings.setOnboardingCompleted() }
    }
}
