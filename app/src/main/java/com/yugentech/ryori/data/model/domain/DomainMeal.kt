package com.yugentech.ryori.data.model.domain

data class DomainMeals(
    val domainMeals: List<DomainMeal?>?
)

data class DomainMeal(
    val area: String?,
    val category: String?,
    val instructions: String?,
    val name: String?,
    val image: String?,
    val tags: String?,
    val ingredients: List<String>,
    val measures: List<String>
)