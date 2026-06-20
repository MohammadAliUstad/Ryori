package com.yugentech.ryori.api.model.domain

data class Meal(
    val id: String,
    val name: String?,
    val category: String?,
    val area: String?,
    val instructions: String?,
    val image: String?,
    val tags: List<String>,
    val youtubeUrl: String?,
    val ingredients: List<Ingredient>,
    val sourceUrl: String?
)

data class Ingredient(
    val name: String,
    val measure: String
)