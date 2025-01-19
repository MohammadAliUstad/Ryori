package com.yugentech.ryori.repository

import com.yugentech.ryori.data.datasource.RemoteDataSource
import com.yugentech.ryori.data.model.domain.DomainAreas
import com.yugentech.ryori.data.model.domain.DomainCategories
import com.yugentech.ryori.data.model.domain.DomainMeal
import com.yugentech.ryori.data.model.domain.DomainMeals
import jakarta.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : MealRepository {

    override suspend fun getRandomMeal(): DomainMeal? {
        return try {
            val remoteMeal = remoteDataSource.getRandomMeal()
            remoteMeal
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMealCategories(): DomainCategories? {
        return try {
            remoteDataSource.getMealCategories()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMealAreas(): DomainAreas? {
        return try {
            remoteDataSource.getMealAreas()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMealsByCategory(category: String): DomainMeals? {
        return try {
            remoteDataSource.getMealsByCategory(category)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMealsByArea(area: String): DomainMeals? {
        return try {
            remoteDataSource.getMealsByArea(area)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun searchMealByName(name: String): DomainMeal? {
        return try {
            remoteDataSource.getMealByName(name)
        } catch (e: Exception) {
            null
        }
    }
}