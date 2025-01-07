package com.example.ryori.data.datasource

import com.example.ryori.data.model.domain.DomainAreas
import com.example.ryori.data.model.domain.DomainCategories
import com.example.ryori.data.model.domain.DomainMeal
import com.example.ryori.data.model.domain.DomainMeals

interface RemoteDataSource {
    suspend fun getRandomMeal(): DomainMeal?
    suspend fun getMealCategories(): DomainCategories?
    suspend fun getMealsByCategory(category: String): DomainMeals?
    suspend fun getMealsByArea(area: String): DomainMeals?
    suspend fun getMealAreas(): DomainAreas?
    suspend fun getMealByName(name: String): DomainMeal?
}