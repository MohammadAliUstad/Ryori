package com.yugentech.ryori.api.repository

import com.yugentech.ryori.api.model.domain.Area
import com.yugentech.ryori.api.model.domain.Category
import com.yugentech.ryori.api.model.domain.Meal

interface FoodRepository {
    suspend fun getRandomMeal(): Result<Meal>
    suspend fun getMealCategories(): Result<List<Category>>
    suspend fun getMealAreas(): Result<List<Area>>
    suspend fun getMealsByCategory(category: String): Result<List<Meal>>
    suspend fun getMealsByArea(area: String): Result<List<Meal>>
    suspend fun getMealByName(name: String): Result<Meal>
}
