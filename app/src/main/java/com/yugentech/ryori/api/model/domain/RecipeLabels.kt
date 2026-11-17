package com.yugentech.ryori.api.model.domain

import com.yugentech.ryori.R
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable

// TheCocktailDB category names, with friendlier labels for the non-alcoholic drinks Ryori
// shows. "Cocktail" becomes "Mocktails" because only non-alcoholic entries are kept.
object DrinkCategories {
    val all: List<Pair<String, Int>> = listOf(
        "Cocktail" to R.string.drink_mocktails,
        "Shake" to R.string.drink_shakes,
        "Coffee / Tea" to R.string.drink_coffee_tea,
        "Cocoa" to R.string.drink_cocoa,
        "Soft Drink" to R.string.drink_soft_drinks,
        "Punch / Party Drink" to R.string.drink_punches,
        "Ordinary Drink" to R.string.drink_everyday,
        "Other / Unknown" to R.string.drink_more
    )

    // The friendly label in the app's language; a category we don't know keeps its own name.
    @Composable
    fun label(category: String): String =
        all.firstOrNull { it.first.equals(category, ignoreCase = true) }?.second
            ?.let { stringResource(it) } ?: category
}

// Flag emoji for TheMealDB's cuisine names ("areas").
object CuisineFlags {
    private val countryCodes = mapOf(
        "Algerian" to "DZ", "American" to "US", "Argentinian" to "AR", "Australian" to "AU",
        "British" to "GB", "Canadian" to "CA", "Chinese" to "CN", "Croatian" to "HR",
        "Dutch" to "NL", "Egyptian" to "EG", "Filipino" to "PH", "French" to "FR",
        "Greek" to "GR", "Indian" to "IN", "Irish" to "IE", "Italian" to "IT",
        "Jamaican" to "JM", "Japanese" to "JP", "Kenyan" to "KE", "Malaysian" to "MY",
        "Mexican" to "MX", "Moroccan" to "MA", "Norwegian" to "NO", "Polish" to "PL",
        "Portuguese" to "PT", "Russian" to "RU", "Saudi Arabian" to "SA", "Slovakian" to "SK",
        "Spanish" to "ES", "Syrian" to "SY", "Thai" to "TH", "Tunisian" to "TN",
        "Turkish" to "TR", "Ukrainian" to "UA", "Uruguayan" to "UY", "Venezulan" to "VE",
        "Venezuelan" to "VE", "Vietnamese" to "VN"
    )

    fun flag(area: String): String {
        val code = countryCodes[area] ?: return "🍽️"
        return code.uppercase().map { char ->
            String(Character.toChars(0x1F1E6 + (char - 'A')))
        }.joinToString("")
    }
}

// Title for a recipe list screen, from what it's filtered by.
@Composable
fun RecipeFilter.titleFor(value: String): String = when (this) {
    RecipeFilter.CATEGORY -> value
    RecipeFilter.AREA -> "${CuisineFlags.flag(value)} $value"
    RecipeFilter.INGREDIENT -> stringResource(R.string.list_title_ingredient, value)
    RecipeFilter.LETTER -> stringResource(R.string.list_title_letter, value)
    RecipeFilter.DRINK_CATEGORY -> DrinkCategories.label(value)
    RecipeFilter.DRINKS -> stringResource(R.string.list_title_drinks)
}
