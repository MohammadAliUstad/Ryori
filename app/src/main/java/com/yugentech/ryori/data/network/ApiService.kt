package com.yugentech.ryori.data.network

import com.yugentech.ryori.data.model.remote.RemoteAreas
import com.yugentech.ryori.data.model.remote.RemoteCategories
import com.yugentech.ryori.data.model.remote.RemoteMeals
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("random.php")
    suspend fun getRandomMeal(): RemoteMeals

    @GET("categories.php")
    suspend fun getMealCategories(): RemoteCategories

    @GET("list.php?a=list")
    suspend fun getMealAreas(): RemoteAreas

    @GET("filter.php")
    suspend fun getMealsByArea(@Query("a") area: String): RemoteMeals

    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String): RemoteMeals

    @GET("search.php")
    suspend fun getMealByName(@Query("s") mealName: String): RemoteMeals
}