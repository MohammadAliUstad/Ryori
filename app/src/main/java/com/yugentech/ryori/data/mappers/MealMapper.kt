package com.yugentech.ryori.data.mappers

import com.yugentech.ryori.data.model.domain.DomainMeal
import com.yugentech.ryori.data.model.domain.DomainMeals
import com.yugentech.ryori.data.model.remote.RemoteMeal
import com.yugentech.ryori.data.model.remote.RemoteMeals

fun RemoteMeals.toDomainMeals(): DomainMeals {
    return DomainMeals(
        domainMeals = meals?.mapNotNull { remoteMeal ->
            remoteMeal?.toDomainMeal()
        } ?: emptyList()
    )
}

fun RemoteMeal.toDomainMeal(): DomainMeal {
    val ingredients = listOfNotNull(
        strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
        strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
        strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
        strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
    )
    val measures = listOfNotNull(
        strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5,
        strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10,
        strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15,
        strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20
    )

    return DomainMeal(
        area = strArea,
        category = strCategory,
        instructions = formatInstructions(strInstructions ?: ""),
        name = strMeal,
        image = strMealThumb,
        tags = strTags,
        ingredients = ingredients,
        measures = measures
    )
}

fun formatInstructions(rawInstructions: String?): String {
    if (rawInstructions.isNullOrBlank()) return "No instructions provided."

    val sanitizedInstructions =
        rawInstructions.replace(Regex("^\\d+[.:\\s]"), "").replace(Regex("\\n\\d+[.:\\s]"), "\n")

    val steps = sanitizedInstructions.split(Regex("\\.\\s|\\n")).filter { it.isNotBlank() }

    return steps.mapIndexed { index, step ->
        "${index + 1}. ${step.trim()}"
    }.joinToString("\n")
}