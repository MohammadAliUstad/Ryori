package com.yugentech.ryori.api.model.domain

import android.net.Uri

// Meals (TheMealDB) and drinks (TheCocktailDB) share one UI model, so a single details
// screen and a single list screen serve both.
enum class RecipeType { MEAL, DRINK }

// What a list / grid needs. Filter endpoints on both APIs only return these three fields.
data class RecipeSummary(
    val id: String,
    val type: RecipeType,
    val name: String,
    val image: String?
)

data class RecipeIngredient(
    val name: String,
    val measure: String,
    val image: String
)

data class Recipe(
    val id: String,
    val type: RecipeType,
    val name: String,
    val image: String?,
    val category: String?,
    val area: String?,
    val drinkStyle: String?,
    val glass: String?,
    val tags: List<String>,
    val ingredients: List<RecipeIngredient>,
    val steps: List<String>,
    val videoUrl: String?,
    val sourceUrl: String?
)

// What a recipe list is filtered by. The value travels in the route, so it's URL-encoded there.
enum class RecipeFilter {
    CATEGORY,
    AREA,
    INGREDIENT,
    LETTER,
    DRINK_CATEGORY,
    DRINKS
}

data class IngredientInfo(
    val name: String,
    val image: String
)

object RecipeImages {
    fun mealIngredient(name: String): String =
        "https://www.themealdb.com/images/ingredients/${Uri.encode(name)}-Small.png"

    fun drinkIngredient(name: String): String =
        "https://www.thecocktaildb.com/images/ingredients/${Uri.encode(name)}-Small.png"
}
