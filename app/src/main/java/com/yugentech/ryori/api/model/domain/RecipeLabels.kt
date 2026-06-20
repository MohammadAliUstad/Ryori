package com.yugentech.ryori.api.model.domain

// TheCocktailDB category names, with friendlier labels for the non-alcoholic drinks Ryori
// shows. "Cocktail" becomes "Mocktails" because only non-alcoholic entries are kept.
object DrinkCategories {
    val all: List<Pair<String, String>> = listOf(
        "Cocktail" to "Mocktails",
        "Shake" to "Shakes",
        "Coffee / Tea" to "Coffee & Tea",
        "Cocoa" to "Cocoa",
        "Soft Drink" to "Soft Drinks",
        "Punch / Party Drink" to "Punches",
        "Ordinary Drink" to "Everyday Drinks",
        "Other / Unknown" to "More Drinks"
    )

    fun label(category: String): String =
        all.firstOrNull { it.first.equals(category, ignoreCase = true) }?.second ?: category
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
fun RecipeFilter.titleFor(value: String): String = when (this) {
    RecipeFilter.CATEGORY -> value
    RecipeFilter.AREA -> "${CuisineFlags.flag(value)} $value"
    RecipeFilter.INGREDIENT -> "Cooking with $value"
    RecipeFilter.LETTER -> "Recipes: $value"
    RecipeFilter.DRINK_CATEGORY -> DrinkCategories.label(value)
    RecipeFilter.DRINKS -> "Drinks & Mocktails"
}
