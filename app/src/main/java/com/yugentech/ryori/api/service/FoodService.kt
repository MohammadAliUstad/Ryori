package com.yugentech.ryori.api.service

import com.yugentech.ryori.api.cache.CachePolicy
import com.yugentech.ryori.api.cache.CachedApi
import com.yugentech.ryori.api.model.remote.RemoteAreas
import com.yugentech.ryori.api.model.remote.RemoteCategories
import com.yugentech.ryori.api.model.remote.RemoteIngredients
import com.yugentech.ryori.api.model.remote.RemoteMeals

// TheMealDB (free test key "1"). https://www.themealdb.com/api.php
// Every call goes through CachedApi with a TTL suited to how often that data changes.
class FoodService(private val api: CachedApi) {

    companion object {
        private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
    }

    suspend fun getRandomMeal(): RemoteMeals =
        api.get("${BASE_URL}random.php", ttl = CachePolicy.NONE)

    suspend fun getMealCategories(): RemoteCategories =
        api.get("${BASE_URL}categories.php", ttl = CachePolicy.STATIC)

    suspend fun getMealAreas(): RemoteAreas =
        api.get("${BASE_URL}list.php", mapOf("a" to "list"), CachePolicy.STATIC)

    suspend fun getIngredients(): RemoteIngredients =
        api.get("${BASE_URL}list.php", mapOf("i" to "list"), CachePolicy.STATIC)

    suspend fun getMealsByArea(area: String): RemoteMeals =
        api.get("${BASE_URL}filter.php", mapOf("a" to area), CachePolicy.LISTS)

    suspend fun getMealsByCategory(category: String): RemoteMeals =
        api.get("${BASE_URL}filter.php", mapOf("c" to category), CachePolicy.LISTS)

    suspend fun getMealsByIngredient(ingredient: String): RemoteMeals =
        api.get("${BASE_URL}filter.php", mapOf("i" to ingredient), CachePolicy.LISTS)

    suspend fun getMealsByFirstLetter(letter: String): RemoteMeals =
        api.get("${BASE_URL}search.php", mapOf("f" to letter), CachePolicy.LISTS)

    suspend fun searchMeals(query: String): RemoteMeals =
        api.get("${BASE_URL}search.php", mapOf("s" to query), CachePolicy.SEARCH)

    suspend fun getMealById(id: String): RemoteMeals =
        api.get("${BASE_URL}lookup.php", mapOf("i" to id), CachePolicy.STATIC)
}
