package com.yugentech.ryori.api.repository

import com.yugentech.ryori.api.model.domain.Area
import com.yugentech.ryori.api.model.domain.Category
import com.yugentech.ryori.api.model.domain.Ingredient
import com.yugentech.ryori.api.model.domain.Meal
import com.yugentech.ryori.api.model.remote.RemoteMeal
import com.yugentech.ryori.api.service.FoodService

class FoodRepositoryImpl(private val service: FoodService) : FoodRepository {

    override suspend fun getRandomMeal(): Result<Meal> = runCatching {
        service.getRandomMeal().meals?.firstOrNull()?.toMeal()
            ?: error("No meal found")
    }

    override suspend fun getMealCategories(): Result<List<Category>> = runCatching {
        service.getMealCategories().categories?.mapNotNull { remote ->
            remote?.let {
                Category(
                    id = it.idCategory ?: "",
                    name = it.strCategory,
                    description = it.strCategoryDescription,
                    image = it.strCategoryThumb
                )
            }
        } ?: emptyList()
    }

    override suspend fun getMealAreas(): Result<List<Area>> = runCatching {
        service.getMealAreas().meals?.mapNotNull { remote ->
            remote.strArea?.let { Area(it) }
        } ?: emptyList()
    }

    override suspend fun getMealsByCategory(category: String): Result<List<Meal>> = runCatching {
        service.getMealsByCategory(category).meals?.mapNotNull { it?.toMeal() } ?: emptyList()
    }

    override suspend fun getMealsByArea(area: String): Result<List<Meal>> = runCatching {
        service.getMealsByArea(area).meals?.mapNotNull { it?.toMeal() } ?: emptyList()
    }

    override suspend fun getMealByName(name: String): Result<Meal> = runCatching {
        service.searchMeals(name).meals?.firstOrNull()?.toMeal()
            ?: error("Meal not found")
    }

    private fun RemoteMeal.toMeal(): Meal {
        val ingredients = listOf(
            strIngredient1 to strMeasure1, strIngredient2 to strMeasure2,
            strIngredient3 to strMeasure3, strIngredient4 to strMeasure4,
            strIngredient5 to strMeasure5, strIngredient6 to strMeasure6,
            strIngredient7 to strMeasure7, strIngredient8 to strMeasure8,
            strIngredient9 to strMeasure9, strIngredient10 to strMeasure10,
            strIngredient11 to strMeasure11, strIngredient12 to strMeasure12,
            strIngredient13 to strMeasure13, strIngredient14 to strMeasure14,
            strIngredient15 to strMeasure15, strIngredient16 to strMeasure16,
            strIngredient17 to strMeasure17, strIngredient18 to strMeasure18,
            strIngredient19 to strMeasure19, strIngredient20 to strMeasure20
        ).mapNotNull { (name, measure) ->
            if (!name.isNullOrBlank()) Ingredient(name, measure ?: "") else null
        }
        return Meal(
            id = idMeal ?: "",
            name = strMeal,
            category = strCategory,
            area = strArea,
            instructions = strInstructions,
            image = strMealThumb,
            tags = strTags?.split(",")?.map { it.trim() } ?: emptyList(),
            youtubeUrl = strYoutube,
            ingredients = ingredients,
            sourceUrl = strSource
        )
    }
}
