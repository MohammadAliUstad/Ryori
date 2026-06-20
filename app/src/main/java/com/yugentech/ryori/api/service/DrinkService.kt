package com.yugentech.ryori.api.service

import com.yugentech.ryori.api.cache.CachePolicy
import com.yugentech.ryori.api.cache.CachedApi
import com.yugentech.ryori.api.model.remote.RemoteDrinks

// TheCocktailDB (free test key "1"), TheMealDB's sister API for drinks.
// https://www.thecocktaildb.com/api.php
class DrinkService(private val api: CachedApi) {

    companion object {
        private const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"
    }

    suspend fun getNonAlcoholicDrinks(): RemoteDrinks =
        api.get("${BASE_URL}filter.php", mapOf("a" to "Non_Alcoholic"), CachePolicy.LISTS)

    suspend fun getDrinksByCategory(category: String): RemoteDrinks =
        api.get("${BASE_URL}filter.php", mapOf("c" to category), CachePolicy.LISTS)

    suspend fun searchDrinks(query: String): RemoteDrinks =
        api.get("${BASE_URL}search.php", mapOf("s" to query), CachePolicy.SEARCH)

    suspend fun getDrinkById(id: String): RemoteDrinks =
        api.get("${BASE_URL}lookup.php", mapOf("i" to id), CachePolicy.STATIC)

    suspend fun getRandomDrink(): RemoteDrinks =
        api.get("${BASE_URL}random.php", ttl = CachePolicy.NONE)
}
