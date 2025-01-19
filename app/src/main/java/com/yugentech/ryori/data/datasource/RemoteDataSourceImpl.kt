package com.yugentech.ryori.data.datasource

import com.yugentech.ryori.data.mappers.toDomainAreas
import com.yugentech.ryori.data.mappers.toDomainCategories
import com.yugentech.ryori.data.mappers.toDomainMeals
import com.yugentech.ryori.data.model.domain.DomainAreas
import com.yugentech.ryori.data.model.domain.DomainCategories
import com.yugentech.ryori.data.model.domain.DomainMeal
import com.yugentech.ryori.data.model.domain.DomainMeals
import com.yugentech.ryori.data.network.ApiService
import jakarta.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : RemoteDataSource {

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