package com.yugentech.ryori.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val error: String? = null,
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
            _uiState.update { it.copy(isLoading = true, error = null) }

            val featured = async { repository.getRandomMeals(count = 5) }
            val categories = async { repository.getCategories() }
            val drinks = async { repository.getDrinks() }
            val cuisine = async {
                val area = repository.getAreas().getOrNull()?.randomOrNull()?.name
                area to area?.let {
                    repository.getRecipes(RecipeFilter.AREA, it).getOrNull()?.shuffled()?.take(10)
                }.orEmpty()
            }
            val category = async {
                // Only categories the repository still offers (vegetarian mode hides the meat ones).
                val available = categories.await().getOrNull().orEmpty().mapTo(HashSet()) { it.name }
                val name = spotlightCategories.filter { it in available }.randomOrNull()
                    ?: return@async (null as String?) to emptyList<RecipeSummary>()
                name to repository.getRecipes(RecipeFilter.CATEGORY, name).getOrNull()?.shuffled()?.take(10).orEmpty()
            }

            val featuredResult = featured.await()
            val (cuisineName, cuisineRecipes) = cuisine.await()
            val (categoryName, categoryRecipes) = category.await()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    // The hero carousel is the heart of the screen, so only its failure counts
                    // as a screen error; other rows just hide when they can't load.
                    error = featuredResult.exceptionOrNull()?.let { e -> e.message ?: "Couldn't load recipes" },
                    featured = featuredResult.getOrDefault(emptyList()),
                    cuisineSpotlight = cuisineName.takeIf { cuisineRecipes.isNotEmpty() },
                    cuisineRecipes = cuisineRecipes,
                    categorySpotlight = categoryName.takeIf { categoryRecipes.isNotEmpty() },
                    categoryRecipes = categoryRecipes,
                    drinks = drinks.await().getOrDefault(emptyList()).shuffled().take(10),
                    categories = categories.await().getOrDefault(emptyList())
                )
            }
        }
    }

    // Fresh set of featured recipes without reloading the rest of the screen.
    fun refreshFeatured() {
        if (_uiState.value.isRefreshingFeatured) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshingFeatured = true) }
            repository.getRandomMeals(count = 5).onSuccess { recipes ->
                _uiState.update { it.copy(featured = recipes) }
            }
            _uiState.update { it.copy(isRefreshingFeatured = false) }
        }
    }

    fun surprise(onFound: (RecipeType, String) -> Unit) {
        if (_uiState.value.isSurpriseLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSurpriseLoading = true) }
            repository.getRandomRecipeId(RecipeType.MEAL).onSuccess { id -> onFound(RecipeType.MEAL, id) }
            _uiState.update { it.copy(isSurpriseLoading = false) }
        }
    }
}
