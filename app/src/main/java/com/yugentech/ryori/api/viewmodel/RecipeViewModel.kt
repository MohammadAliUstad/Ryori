package com.yugentech.ryori.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.ryori.api.model.domain.Recipe
import com.yugentech.ryori.api.model.domain.RecipeSummary
import com.yugentech.ryori.api.model.domain.RecipeType
import com.yugentech.ryori.api.repository.RecipeRepository
import com.yugentech.ryori.data.settings.SettingsRepository
import com.yugentech.ryori.data.stats.KitchenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val recipe: Recipe? = null,
    val related: List<RecipeSummary> = emptyList(),
    // Indices of ingredients the user has ticked off while cooking.
    val checkedIngredients: Set<Int> = emptySet()
)

class RecipeViewModel(
    private val repository: RecipeRepository,
    private val kitchen: KitchenRepository,
    settings: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    // "Keep screen on" preference: the screen stays awake while a recipe is open.
    val keepScreenOn: StateFlow<Boolean> = settings.settings
        .map { it.keepScreenOn }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private var loadedKey: Pair<RecipeType, String>? = null

    fun load(type: RecipeType, id: String, force: Boolean = false) {
        val key = type to id
        if (!force && key == loadedKey && _uiState.value.recipe != null) return
        loadedKey = key

        viewModelScope.launch {
            _uiState.update { RecipeUiState(isLoading = true) }
            repository.getRecipe(type, id)
                .onSuccess { recipe ->
                    _uiState.update { it.copy(isLoading = false, recipe = recipe) }
                    // Recently viewed + stats (loadedKey above keeps recompositions from counting twice).
                    kitchen.recordRecipeView(recipe)
                    val related = repository.getRelated(recipe, limit = 10).getOrDefault(emptyList())
                    _uiState.update { it.copy(related = related) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message ?: "Couldn't load this recipe") }
                }
        }
    }

    fun toggleIngredient(index: Int) {
        val ticking = index !in _uiState.value.checkedIngredients
        _uiState.update {
            val checked = it.checkedIngredients
            it.copy(checkedIngredients = if (index in checked) checked - index else checked + index)
        }
        if (ticking) viewModelScope.launch { kitchen.recordIngredientTicked() }
    }
}
