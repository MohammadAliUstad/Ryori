package com.yugentech.ryori.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.ryori.api.error.AppError
import com.yugentech.ryori.api.error.toAppError
import com.yugentech.ryori.api.model.domain.RecipeFilter
import com.yugentech.ryori.api.model.domain.RecipeSummary
import com.yugentech.ryori.api.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeListUiState(
    val isLoading: Boolean = true,
    val error: AppError? = null,
    val recipes: List<RecipeSummary> = emptyList()
)

class RecipeListViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState: StateFlow<RecipeListUiState> = _uiState.asStateFlow()

    private var loadedKey: Pair<RecipeFilter, String>? = null

    fun load(filter: RecipeFilter, value: String, force: Boolean = false) {
        val key = filter to value
        if (!force && key == loadedKey && _uiState.value.recipes.isNotEmpty()) return
        loadedKey = key

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getRecipes(filter, value)
                .onSuccess { recipes -> _uiState.update { it.copy(isLoading = false, recipes = recipes) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.toAppError()) } }
        }
    }
}
