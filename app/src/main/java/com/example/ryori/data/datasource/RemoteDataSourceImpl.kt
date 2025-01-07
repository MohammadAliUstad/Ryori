package com.example.ryori.data.datasource

import com.example.ryori.data.mappers.toDomainAreas
import com.example.ryori.data.mappers.toDomainCategories
import com.example.ryori.data.mappers.toDomainMeals
import com.example.ryori.data.model.domain.DomainAreas
import com.example.ryori.data.model.domain.DomainCategories
import com.example.ryori.data.model.domain.DomainMeal
import com.example.ryori.data.model.domain.DomainMeals
import com.example.ryori.data.network.ApiService
import jakarta.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : RemoteDataSource {

    companion object {
        private const val TAG = "RemoteDataSourceImpl"
    }

    override suspend fun getRandomMeal(): DomainMeal? {
        return try {
            val response = apiService.getRandomMeal()
            val domainMeal = response.toDomainMeals().domainMeals?.firstOrNull()
            domainMeal
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMealCategories(): DomainCategories? {
        return try {
            val response = apiService.getMealCategories()
            val domainCategories = response.toDomainCategories()
            domainCategories
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMealsByCategory(category: String): DomainMeals? {
        return try {
            val response = apiService.getMealsByCategory(category)
            val domainMeals = response.toDomainMeals()
            domainMeals
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMealAreas(): DomainAreas? {
        return try {
            val response = apiService.getMealAreas()
            val domainAreas = response.toDomainAreas()
            domainAreas
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMealsByArea(area: String): DomainMeals? {
        return try {
            val response = apiService.getMealsByArea(area)
            val domainMeals = response.toDomainMeals()
            domainMeals
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMealByName(name: String): DomainMeal? {
        return try {
            val response = apiService.getMealByName(name)
            val domainMeal = response.toDomainMeals().domainMeals?.firstOrNull()
            domainMeal
        } catch (e: Exception) {
            null
        }
    }
}