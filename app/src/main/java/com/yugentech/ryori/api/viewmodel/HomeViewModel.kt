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

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, notice = null) }

            val featured = async { repository.getRandomMeals(count = 5) }
            val categories = async { repository.getCategories() }
            val drinks = async { repository.getDrinks() }
            val cuisine = async {
                val areas = repository.getAreas()
                val area = areas.getOrNull()?.randomOrNull()?.name
                    ?: return@async Spotlight(null, emptyList(), areas.appErrorOrNull())
                val recipes = repository.getRecipes(RecipeFilter.AREA, area)
                Spotlight(area, recipes.getOrNull()?.shuffled()?.take(10).orEmpty(), recipes.appErrorOrNull())
            }
            val category = async {
                // Only categories the repository still offers (vegetarian mode hides the meat ones).
                val available = categories.await().getOrNull().orEmpty().mapTo(HashSet()) { it.name }
                val name = spotlightCategories.filter { it in available }.randomOrNull()
                    ?: return@async Spotlight(null, emptyList(), null)
                val recipes = repository.getRecipes(RecipeFilter.CATEGORY, name)
                Spotlight(name, recipes.getOrNull()?.shuffled()?.take(10).orEmpty(), recipes.appErrorOrNull())
            }

            val featuredResult = featured.await()
            val cuisineSpot = cuisine.await()
            val categorySpot = category.await()
            val drinksResult = drinks.await()
            val categoriesResult = categories.await()

            // The hero carousel is the heart of the screen, so only its failure counts as a
            // screen error. Other rows just hide when they can't load, with a toast saying why.
            val screenError = featuredResult.appErrorOrNull()
            val rowError = listOfNotNull(
                cuisineSpot.error,
                categorySpot.error,
                drinksResult.appErrorOrNull(),
                categoriesResult.appErrorOrNull()
            ).firstOrNull()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = screenError,
                    notice = if (screenError == null) rowError else null,
                    featured = featuredResult.getOrDefault(emptyList()),
                    cuisineSpotlight = cuisineSpot.name.takeIf { cuisineSpot.recipes.isNotEmpty() },
                    cuisineRecipes = cuisineSpot.recipes,
                    categorySpotlight = categorySpot.name.takeIf { categorySpot.recipes.isNotEmpty() },
                    categoryRecipes = categorySpot.recipes,
                    drinks = drinksResult.getOrDefault(emptyList()).shuffled().take(10),
                    categories = categoriesResult.getOrDefault(emptyList())
                )
            }
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

    private data class Spotlight(val name: String?, val recipes: List<RecipeSummary>, val error: AppError?)
}
