package com.yugentech.ryori.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugentech.ryori.data.model.domain.DomainArea
import com.yugentech.ryori.data.model.domain.DomainCategory
import com.yugentech.ryori.data.model.domain.DomainMeal
import com.yugentech.ryori.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RyoriViewModel @Inject constructor(
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _meal = MutableStateFlow<DomainMeal?>(null)
    val meal: StateFlow<DomainMeal?> = _meal.asStateFlow()

    private val _meals = MutableStateFlow<List<DomainMeal>>(emptyList())
    val meals: StateFlow<List<DomainMeal>> = _meals.asStateFlow()

    private val _categories = MutableStateFlow<List<DomainCategory>>(emptyList())
    val categories: StateFlow<List<DomainCategory>> = _categories.asStateFlow()

    private val _areas = MutableStateFlow<List<DomainArea>>(emptyList())
    val areas: StateFlow<List<DomainArea>> = _areas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchRandomMeal() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _meal.value = mealRepository.getRandomMeal()
            } catch (e: Exception) {
                _error.value = "Failed to fetch random meal: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchCategories() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _categories.value =
                    mealRepository.getMealCategories()?.domainCategories ?: emptyList()
            } catch (e: Exception) {
                _error.value = "Failed to fetch categories: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchAreas() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _areas.value = mealRepository.getMealAreas()?.domainAreas ?: emptyList()
            } catch (e: Exception) {
                _error.value = "Failed to fetch areas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchMealsByCategory(category: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val fetchedMeals = mealRepository.getMealsByCategory(category)?.domainMeals
                _meals.value = fetchedMeals?.filterNotNull() ?: emptyList()
            } catch (e: Exception) {
                _error.value = "Failed to fetch meals by category: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchMealsByArea(area: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val fetchedMeals = mealRepository.getMealsByArea(area)?.domainMeals
                _meals.value = fetchedMeals?.filterNotNull() ?: emptyList()
            } catch (e: Exception) {
                _error.value = "Failed to fetch meals by area: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchMealByName(mealName: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _meal.value = mealRepository.searchMealByName(mealName)
            } catch (e: Exception) {
                _error.value = "Failed to fetch meal by name: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}