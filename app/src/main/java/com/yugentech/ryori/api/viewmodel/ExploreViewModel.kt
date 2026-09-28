package com.yugentech.ryori.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.ryori.api.error.AppError
import com.yugentech.ryori.api.error.appErrorOrNull
import com.yugentech.ryori.api.error.toAppError
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
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExploreUiState(
    val isLoading: Boolean = true,
    val error: AppError? = null,
    // One-off problem shown as a toast: a failed surprise, or sections that couldn't load.
    val notice: AppError? = null,
    val categories: List<Category> = emptyList(),
    val areas: List<Area> = emptyList(),
    val ingredients: List<IngredientInfo> = emptyList(),
    val drinks: List<RecipeSummary> = emptyList(),
    val query: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<RecipeSummary> = emptyList(),
    val searchError: AppError? = null,
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
            _uiState.update { it.copy(isLoading = true, error = null, notice = null) }

            val categories = async { repository.getCategories() }
            val areas = async { repository.getAreas() }
            val ingredients = async { repository.getPopularIngredients(limit = 24) }
            val drinks = async { repository.getDrinks() }

            val categoriesResult = categories.await()
            val areasResult = areas.await()
            val ingredientsResult = ingredients.await()
            val drinksResult = drinks.await()

            // Sections load independently: one failing endpoint just hides its section (with a
            // toast saying why). Only show the error state if nothing at all came back.
            val results = listOf(categoriesResult, areasResult, ingredientsResult, drinksResult)
            val nothingLoaded = results.all { it.isFailure }
            val firstError = results.firstNotNullOfOrNull { it.appErrorOrNull() }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = if (nothingLoaded) firstError else null,
                    notice = if (nothingLoaded) null else firstError,
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
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update {
                it.copy(query = query, isSearching = false, searchResults = emptyList(), searchError = null)
            }
            return
        }
        // Searching from the first keystroke, not after the debounce: otherwise the empty
        // results during the delay read as "No recipes found" until the real results land.
        _uiState.update { it.copy(query = query, isSearching = true, searchError = null) }
        searchJob = viewModelScope.launch {
            delay(350)
            val result = repository.search(query.trim())
            // A newer query may have cancelled this one mid-request; don't let it overwrite.
            ensureActive()
            _uiState.update {
                it.copy(
                    isSearching = false,
                    searchResults = result.getOrDefault(emptyList()),
                    searchError = result.appErrorOrNull()
                )
            }
        }
    }

    fun clearSearch() = onQueryChange("")

    // Re-runs the current search, e.g. after a connection error.
    fun retrySearch() = onQueryChange(_uiState.value.query)

    fun dismissNotice() = _uiState.update { it.copy(notice = null) }

    fun surprise(type: RecipeType, onFound: (RecipeType, String) -> Unit) {
        if (_uiState.value.surpriseInProgress != null) return
        viewModelScope.launch {
            _uiState.update { it.copy(surpriseInProgress = type) }
            repository.getRandomRecipeId(type)
                .onSuccess { id -> onFound(type, id) }
                .onFailure { e -> _uiState.update { it.copy(notice = e.toAppError()) } }
            _uiState.update { it.copy(surpriseInProgress = null) }
        }
    }
}
