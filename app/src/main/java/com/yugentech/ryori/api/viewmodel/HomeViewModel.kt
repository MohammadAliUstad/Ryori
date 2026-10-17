package com.yugentech.ryori.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.ryori.api.error.AppError
import com.yugentech.ryori.api.error.appErrorOrNull
import com.yugentech.ryori.api.error.toAppError
import com.yugentech.ryori.api.model.domain.Category
import com.yugentech.ryori.api.model.domain.Recipe
import com.yugentech.ryori.api.model.domain.RecipeFilter
import com.yugentech.ryori.api.model.domain.RecipeSummary
import com.yugentech.ryori.api.model.domain.RecipeType
import com.yugentech.ryori.api.repository.RecipeRepository
import com.yugentech.ryori.data.settings.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val error: AppError? = null,
    // One-off problem shown as a toast: a failed shuffle or surprise, or rows that couldn't load.
    val notice: AppError? = null,
    val featured: List<Recipe> = emptyList(),
    val isRefreshingFeatured: Boolean = false,
    val cuisineSpotlight: String? = null,
    val cuisineRecipes: List<RecipeSummary> = emptyList(),
    val categorySpotlight: String? = null,
    val categoryRecipes: List<RecipeSummary> = emptyList(),
    val drinks: List<RecipeSummary> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isSurpriseLoading: Boolean = false
)

class HomeViewModel(
    private val repository: RecipeRepository,
    settings: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Categories picked for the "category of the day" row: broad, photogenic ones that
    // always have plenty of recipes.
    private val spotlightCategories = listOf(
        "Chicken", "Seafood", "Pasta", "Vegetarian", "Dessert", "Beef", "Breakfast", "Lamb"
    )

    init {
        load()
        // Vegetarian mode changes what every row may show, so start over when it flips.
        viewModelScope.launch { settings.vegetarianMode.drop(1).collect { load() } }
    }

    // A reload (retry, vegetarian mode flip) replaces the one in flight, so rows from the old
    // load can't land on top of the new one.
    private var loadJob: Job? = null

    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, notice = null) }

            // Each part lands on screen as soon as it's ready, so the hero carousel isn't held up
            // by the slower rows below it.
            val featured = async {
                repository.getRandomMeals(count = 5).also { result ->
                    // The hero carousel is the heart of the screen, so only its failure counts
                    // as a screen error. Other rows just hide, with a toast saying why.
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.appErrorOrNull(),
                            featured = result.getOrDefault(emptyList())
                        )
                    }
                }
            }
            val categories = async {
                repository.getCategories().also { result ->
                    _uiState.update { it.copy(categories = result.getOrDefault(emptyList())) }
                }
            }
            val drinks = async {
                repository.getDrinks().also { result ->
                    _uiState.update { it.copy(drinks = result.getOrDefault(emptyList()).shuffled().take(10)) }
                }
            }
            val cuisine = async {
                // The featured meals' cuisines certainly have recipes, so try those first.
                val preferred = featured.await().getOrNull().orEmpty().mapNotNull { it.area }
                val result = repository.getCuisineSpotlight(limit = 10, preferred = preferred)
                val (name, recipes) = result.getOrNull() ?: (null to emptyList())
                _uiState.update { it.copy(cuisineSpotlight = name, cuisineRecipes = recipes) }
                result.appErrorOrNull()
            }
            val category = async {
                // Only categories the repository still offers (vegetarian mode hides the meat ones).
                val available = categories.await().getOrNull().orEmpty().mapTo(HashSet()) { it.name }
                val name = spotlightCategories.filter { it in available }.randomOrNull()
                    ?: return@async null
                val recipes = repository.getRecipes(RecipeFilter.CATEGORY, name)
                val picked = recipes.getOrNull()?.shuffled()?.take(10).orEmpty()
                _uiState.update {
                    it.copy(categorySpotlight = name.takeIf { picked.isNotEmpty() }, categoryRecipes = picked)
                }
                recipes.appErrorOrNull()
            }

            val screenError = featured.await().appErrorOrNull()
            val rowError = listOfNotNull(
                cuisine.await(),
                category.await(),
                drinks.await().appErrorOrNull(),
                categories.await().appErrorOrNull()
            ).firstOrNull()

            if (screenError == null && rowError != null) _uiState.update { it.copy(notice = rowError) }
        }
    }

    // Fresh set of featured recipes without reloading the rest of the screen.
    fun refreshFeatured() {
        if (_uiState.value.isRefreshingFeatured) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshingFeatured = true) }
            repository.getRandomMeals(count = 5)
                .onSuccess { recipes -> _uiState.update { it.copy(featured = recipes) } }
                .onFailure { e -> _uiState.update { it.copy(notice = e.toAppError()) } }
            _uiState.update { it.copy(isRefreshingFeatured = false) }
        }
    }

    fun surprise(onFound: (RecipeType, String) -> Unit) {
        if (_uiState.value.isSurpriseLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSurpriseLoading = true) }
            repository.getRandomRecipeId(RecipeType.MEAL)
                .onSuccess { id -> onFound(RecipeType.MEAL, id) }
                .onFailure { e -> _uiState.update { it.copy(notice = e.toAppError()) } }
            _uiState.update { it.copy(isSurpriseLoading = false) }
        }
    }

    fun dismissNotice() = _uiState.update { it.copy(notice = null) }
}
