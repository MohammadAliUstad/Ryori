package com.yugentech.ryori.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.ryori.api.model.domain.RecipeSummary
import com.yugentech.ryori.data.stats.KitchenRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecentlyViewedViewModel(
    private val kitchen: KitchenRepository
) : ViewModel() {

    // null until the first read from Room, so the screen can tell "loading" from "empty".
    val recipes: StateFlow<List<RecipeSummary>?> = kitchen.recentRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun clear() {
        viewModelScope.launch { kitchen.clearRecent() }
    }
}
