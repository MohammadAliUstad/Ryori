package com.yugentech.ryori.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.ryori.api.model.domain.Area
import com.yugentech.ryori.api.model.domain.Category
import com.yugentech.ryori.api.model.domain.IngredientInfo
import com.yugentech.ryori.api.model.domain.RecipeSummary
import com.yugentech.ryori.api.model.domain.RecipeType
import com.yugentech.ryori.api.repository.RecipeRepository
import com.yugentech.ryori.data.settings.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExploreUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val categories: List<Category> = emptyList(),
    val areas: List<Area> = emptyList(),
    val ingredients: List<IngredientInfo> = emptyList(),
    val drinks: List<RecipeSummary> = emptyList(),
    val query: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<RecipeSummary> = emptyList(),
    val surpriseInProgress: RecipeType? = null
) {
    val isSearchMode: Boolean get() = query.isNotBlank()
}

class ExploreViewModel(
    private val repository: RecipeRepository,
    settings: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        load()
        // Vegetarian mode changes the categories, ingredients and search results on offer.
        viewModelScope.launch {
            settings.vegetarianMode.drop(1).collect {
                load()
                _uiState.value.query.takeIf { q -> q.isNotBlank() }?.let(::onQueryChange)
            }
        }
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val categories = async { repository.getCategories() }
            val areas = async { repository.getAreas() }
            val ingredients = async { repository.getPopularIngredients(limit = 24) }
            val drinks = async { repository.getDrinks() }

            val categoriesResult = categories.await()
            val areasResult = areas.await()
            val ingredientsResult = ingredients.await()
            val drinksResult = drinks.await()

            // Sections load independently: one failing endpoint just hides its section. Only
            // show the error state if nothing at all came back.
            val nothingLoaded = listOf(categoriesResult, areasResult, ingredientsResult, drinksResult)
                .all { it.isFailure }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = if (nothingLoaded) categoriesResult.exceptionOrNull()?.message ?: "Something went wrong" else null,
                    categories = categoriesResult.getOrDefault(emptyList()),
                    areas = areasResult.getOrDefault(emptyList()),
                    ingredients = ingredientsResult.getOrDefault(emptyList()),
                    drinks = drinksResult.getOrDefault(emptyList()).shuffled().take(12)
                )
            }
        }
    }

    // Debounced search across meals and drinks as the user types.
    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update { it.copy(isSearching = false, searchResults = emptyList()) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(350)
            _uiState.update { it.copy(isSearching = true) }
            val results = repository.search(query.trim()).getOrDefault(emptyList())
            _uiState.update { it.copy(isSearching = false, searchResults = results) }
        }
    }

    fun clearSearch() = onQueryChange("")

    fun surprise(type: RecipeType, onFound: (RecipeType, String) -> Unit) {
        if (_uiState.value.surpriseInProgress != null) return
        viewModelScope.launch {
            _uiState.update { it.copy(surpriseInProgress = type) }
            repository.getRandomRecipeId(type).onSuccess { id -> onFound(type, id) }
            _uiState.update { it.copy(surpriseInProgress = null) }
        }
    }
}
