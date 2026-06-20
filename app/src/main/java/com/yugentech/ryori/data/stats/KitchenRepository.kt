package com.yugentech.ryori.data.stats

import com.yugentech.ryori.api.model.domain.Recipe
import com.yugentech.ryori.api.model.domain.RecipeSummary
import com.yugentech.ryori.api.model.domain.RecipeType
import com.yugentech.ryori.data.local.KitchenStatsDao
import com.yugentech.ryori.data.local.RecentRecipeDao
import com.yugentech.ryori.data.local.RecentRecipeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import timber.log.Timber

data class KitchenStats(
    val recipesViewed: Long = 0,
    val ingredientsTicked: Long = 0,
    val favouriteCuisine: String? = null
)

// Everything the app learns from how the user cooks: recently viewed recipes and a few
// running stats, all stored locally in Room. Recording never throws: stats are a nice-to-have
// and must never break opening a recipe.
class KitchenRepository(
    private val recentDao: RecentRecipeDao,
    private val statsDao: KitchenStatsDao
) {

    val stats: Flow<KitchenStats> = combine(
        statsDao.counter(STAT_RECIPES_VIEWED),
        statsDao.counter(STAT_INGREDIENTS_TICKED),
        statsDao.favouriteCuisine()
    ) { viewed, ticked, cuisine ->
        KitchenStats(recipesViewed = viewed, ingredientsTicked = ticked, favouriteCuisine = cuisine)
    }

    val recentCount: Flow<Int> = recentDao.count()

    fun recentRecipes(limit: Int = MAX_RECENT): Flow<List<RecipeSummary>> =
        recentDao.recent(limit).map { rows ->
            rows.mapNotNull { row ->
                val type = runCatching { RecipeType.valueOf(row.type) }.getOrNull() ?: return@mapNotNull null
                RecipeSummary(id = row.id, type = type, name = row.name, image = row.image)
            }
        }

    suspend fun recordRecipeView(recipe: Recipe) {
        runCatching {
            recentDao.upsert(
                RecentRecipeEntity(
                    key = "${recipe.type.name}_${recipe.id}",
                    id = recipe.id,
                    type = recipe.type.name,
                    name = recipe.name,
                    image = recipe.image,
                    viewedAtMillis = System.currentTimeMillis()
                )
            )
            recentDao.trim(keep = MAX_RECENT)
            statsDao.increment(STAT_RECIPES_VIEWED)
            recipe.area?.let { statsDao.incrementCuisine(it) }
        }.onFailure { Timber.w(it, "Couldn't record recipe view") }
    }

    suspend fun recordIngredientTicked() {
        runCatching { statsDao.increment(STAT_INGREDIENTS_TICKED) }
            .onFailure { Timber.w(it, "Couldn't record ingredient tick") }
    }

    suspend fun clearRecent() {
        runCatching { recentDao.clear() }
    }

    private companion object {
        const val STAT_RECIPES_VIEWED = "recipes_viewed"
        const val STAT_INGREDIENTS_TICKED = "ingredients_ticked"
        const val MAX_RECENT = 30
    }
}
