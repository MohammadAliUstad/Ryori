package com.yugentech.ryori.api.repository

import com.yugentech.ryori.api.model.domain.Area
import com.yugentech.ryori.api.model.domain.Category
import com.yugentech.ryori.api.model.domain.IngredientInfo
import com.yugentech.ryori.api.model.domain.Recipe
import com.yugentech.ryori.api.model.domain.RecipeFilter
import com.yugentech.ryori.api.model.domain.RecipeSummary
import com.yugentech.ryori.api.model.domain.RecipeType

// Meals (TheMealDB) and non-alcoholic drinks (TheCocktailDB) behind one interface, for the
// Explore, recipe list and recipe details screens.
interface RecipeRepository {
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getAreas(): Result<List<Area>>
    suspend fun getPopularIngredients(limit: Int): Result<List<IngredientInfo>>
    suspend fun getDrinks(): Result<List<RecipeSummary>>
    suspend fun search(query: String): Result<List<RecipeSummary>>
    suspend fun getRecipe(type: RecipeType, id: String): Result<Recipe>
    suspend fun getRandomRecipeId(type: RecipeType): Result<String>
    suspend fun getRandomMeals(count: Int): Result<List<Recipe>>
    suspend fun getRecipes(filter: RecipeFilter, value: String): Result<List<RecipeSummary>>
    suspend fun getRelated(recipe: Recipe, limit: Int): Result<List<RecipeSummary>>
}
