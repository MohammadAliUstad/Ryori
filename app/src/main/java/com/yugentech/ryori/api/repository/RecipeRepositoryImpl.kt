package com.yugentech.ryori.api.repository

import com.yugentech.ryori.api.error.NotFoundException
import com.yugentech.ryori.api.model.domain.Area
import com.yugentech.ryori.api.model.domain.Category
import com.yugentech.ryori.api.model.domain.IngredientInfo
import com.yugentech.ryori.api.model.domain.Recipe
import com.yugentech.ryori.api.model.domain.RecipeFilter
import com.yugentech.ryori.api.model.domain.RecipeImages
import com.yugentech.ryori.api.model.domain.RecipeIngredient
import com.yugentech.ryori.api.model.domain.RecipeSummary
import com.yugentech.ryori.api.model.domain.RecipeType
import com.yugentech.ryori.api.model.remote.RemoteDrink
import com.yugentech.ryori.api.model.remote.RemoteMeal
import com.yugentech.ryori.api.service.DrinkService
import com.yugentech.ryori.api.service.FoodService
import com.yugentech.ryori.data.settings.SettingsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class RecipeRepositoryImpl(
    private val foodService: FoodService,
    private val drinkService: DrinkService,
    private val settings: SettingsRepository
) : RecipeRepository {

    override suspend fun getCategories(): Result<List<Category>> = runCatching {
        val vegetarian = isVegetarian()
        foodService.getMealCategories().categories.orEmpty().mapNotNull { remote ->
            remote?.let {
                Category(
                    id = it.idCategory.orEmpty(),
                    name = it.strCategory,
                    description = it.strCategoryDescription,
                    image = it.strCategoryThumb
                )
            }
        }.filterNot { vegetarian && it.name.isMeatCategory() }
    }

    // TheMealDB's cuisine list can include cuisines it has no meals for yet, which would open an
    // empty list. Keep only cuisines with at least one recipe. The per-cuisine checks run in
    // parallel and go through the response cache (1 day), so this is cheap after the first run
    // and the cuisine screens then open instantly from the same cached lists.
    override suspend fun getAreas(): Result<List<Area>> = runCatching {
        val names = foodService.getMealAreas().meals.orEmpty()
            .mapNotNull { it.strArea?.trim()?.takeIf(String::isNotEmpty) }
            .filter { it != "Unknown" }
            .distinct()

        coroutineScope {
            names.map { name ->
                async {
                    val hasRecipes = runCatching {
                        foodService.getMealsByArea(name).meals.orEmpty().any { it != null }
                    }.getOrDefault(false)
                    name.takeIf { hasRecipes }
                }
            }.mapNotNull { it.await() }
        }.map { Area(it) }
    }

    // TheMealDB lists ingredients by id, and the lowest ids are the staples (chicken, salmon,
    // beef, pork, ...), so the first entries make a good "popular" row.
    override suspend fun getPopularIngredients(limit: Int): Result<List<IngredientInfo>> =
        runCatching {
            val vegetarian = isVegetarian()
            foodService.getIngredients().meals.orEmpty()
                .mapNotNull { it?.strIngredient?.trim()?.takeIf(String::isNotEmpty) }
                .filterNot { vegetarian && it.isMeatIngredient() }
                .take(limit)
                .map { IngredientInfo(name = it, image = RecipeImages.mealIngredient(it)) }
        }

    override suspend fun getDrinks(): Result<List<RecipeSummary>> = runCatching {
        nonAlcoholicDrinks()
    }

    // Meals and drinks are searched separately; if one side fails the other's results still
    // show. Only when both fail is it an error (so "offline" isn't shown as "no results").
    override suspend fun search(query: String): Result<List<RecipeSummary>> = runCatching {
        coroutineScope {
            val meals = async {
                runCatching { foodService.searchMeals(query).meals.orEmpty().mapNotNull { it?.toSummary() } }
            }
            val drinks = async {
                runCatching {
                    drinkService.searchDrinks(query).drinks.orEmpty()
                        .filterNotNull()
                        .filter { it.isNonAlcoholic() }
                        .mapNotNull { it.toSummary() }
                }
            }
            val mealsResult = meals.await()
            val drinksResult = drinks.await()
            if (mealsResult.isFailure && drinksResult.isFailure) throw mealsResult.exceptionOrNull()!!
            mealsResult.getOrDefault(emptyList()).withoutMeat() + drinksResult.getOrDefault(emptyList())
        }
    }

    override suspend fun getRecipe(type: RecipeType, id: String): Result<Recipe> = runCatching {
        when (type) {
            RecipeType.MEAL -> foodService.getMealById(id).meals?.firstOrNull()?.toRecipe()
            RecipeType.DRINK -> drinkService.getDrinkById(id).drinks?.firstOrNull()?.toRecipe()
        } ?: throw NotFoundException("Recipe $type/$id not found")
    }

    override suspend fun getRandomRecipeId(type: RecipeType): Result<String> = runCatching {
        when (type) {
            RecipeType.MEAL -> if (isVegetarian()) {
                // Pick from the vegetarian-friendly categories rather than rerolling random.php.
                // If every category failed, report why (e.g. offline) rather than "not found".
                var failure: Throwable? = null
                VEGETARIAN_CATEGORIES.shuffled().firstNotNullOfOrNull { category ->
                    runCatching { foodService.getMealsByCategory(category).meals.orEmpty() }
                        .onFailure { failure = it }
                        .getOrDefault(emptyList())
                        .mapNotNull { it?.idMeal }
                        .randomOrNull()
                } ?: failure?.let { throw it }
            } else {
                foodService.getRandomMeal().meals?.firstOrNull()?.idMeal
            }
            // random.php can return alcoholic drinks, so pick from the non-alcoholic list.
            RecipeType.DRINK -> nonAlcoholicDrinks().randomOrNull()?.id
        } ?: throw NotFoundException("No random $type found")
    }

    // TheMealDB's random.php returns one meal per call, so fire them in parallel and drop
    // duplicates. Individual failures are skipped; it only fails if nothing came back, and then
    // with the first failure's cause so the user sees why (offline, timeout...).
    // In vegetarian mode more are fetched than needed, since meat dishes get dropped.
    override suspend fun getRandomMeals(count: Int): Result<List<Recipe>> = runCatching {
        val vegetarian = isVegetarian()
        val attempts = if (vegetarian) count * 3 else count
        val results = coroutineScope {
            List(attempts) {
                async { runCatching { foodService.getRandomMeal().meals?.firstOrNull()?.toRecipe() } }
            }.map { it.await() }
        }
        val meals = results.mapNotNull { it.getOrNull() }
            .distinctBy { it.id }
            .filterNot { vegetarian && it.category.isMeatCategory() }
            .take(count)
        meals.ifEmpty {
            throw results.firstNotNullOfOrNull { it.exceptionOrNull() }
                ?: NotFoundException("No random meals found")
        }
    }

    override suspend fun getRecipes(filter: RecipeFilter, value: String): Result<List<RecipeSummary>> =
        runCatching {
            when (filter) {
                RecipeFilter.CATEGORY ->
                    foodService.getMealsByCategory(value).meals.orEmpty().mapNotNull { it?.toSummary() }.withoutMeat()

                RecipeFilter.AREA ->
                    foodService.getMealsByArea(value).meals.orEmpty().mapNotNull { it?.toSummary() }.withoutMeat()

                RecipeFilter.INGREDIENT ->
                    foodService.getMealsByIngredient(value).meals.orEmpty().mapNotNull { it?.toSummary() }.withoutMeat()

                RecipeFilter.LETTER ->
                    foodService.getMealsByFirstLetter(value).meals.orEmpty().mapNotNull { it?.toSummary() }.withoutMeat()

                // TheCocktailDB categories mix alcoholic and non-alcoholic drinks; keep only the
                // non-alcoholic ones.
                RecipeFilter.DRINK_CATEGORY -> {
                    val allowed = nonAlcoholicDrinks().mapTo(HashSet()) { it.id }
                    drinkService.getDrinksByCategory(value).drinks.orEmpty()
                        .mapNotNull { it?.toSummary() }
                        .filter { it.id in allowed }
                }

                RecipeFilter.DRINKS -> nonAlcoholicDrinks()
            }
        }

    override suspend fun getRelated(recipe: Recipe, limit: Int): Result<List<RecipeSummary>> =
        runCatching {
            val pool = when (recipe.type) {
                RecipeType.MEAL -> recipe.category
                    ?.let { foodService.getMealsByCategory(it).meals.orEmpty().mapNotNull { m -> m?.toSummary() } }
                    .orEmpty()
                    .withoutMeat()

                RecipeType.DRINK -> nonAlcoholicDrinks()
            }
            pool.filter { it.id != recipe.id }.shuffled().take(limit)
        }

    // --- Vegetarian mode -------------------------------------------------------------------

    private suspend fun isVegetarian(): Boolean =
        runCatching { settings.current().vegetarianMode }.getOrDefault(false)

    // TheMealDB list endpoints only return id, name and image, so meat dishes are recognised
    // by being listed in one of the meat categories. Those lists come from the response cache,
    // so this is cheap after the first call. (Dishes filed under e.g. "Side" or "Starter" can
    // still contain meat; the API has no per-dish vegetarian flag.)
    private suspend fun meatMealIds(): Set<String> = coroutineScope {
        MEAT_CATEGORIES.map { category ->
            async {
                runCatching { foodService.getMealsByCategory(category).meals.orEmpty().mapNotNull { it?.idMeal } }
                    .getOrDefault(emptyList())
            }
        }.flatMapTo(HashSet()) { it.await() }
    }

    private suspend fun List<RecipeSummary>.withoutMeat(): List<RecipeSummary> {
        if (!isVegetarian() || none { it.type == RecipeType.MEAL }) return this
        val meat = meatMealIds()
        return filterNot { it.type == RecipeType.MEAL && it.id in meat }
    }

    private fun String?.isMeatCategory(): Boolean =
        this != null && MEAT_CATEGORIES.any { it.equals(this, ignoreCase = true) }

    private fun String.isMeatIngredient(): Boolean {
        val words = lowercase().split(Regex("""[^a-z]+"""))
        return words.any { word -> MEAT_KEYWORDS.any { word.startsWith(it) } }
    }

    // Ryori is a food app, so drinks are limited to TheCocktailDB's non-alcoholic ones. Fetched
    // often (drinks row, category intersections, related), but the response cache keeps it cheap.
    private suspend fun nonAlcoholicDrinks(): List<RecipeSummary> =
        drinkService.getNonAlcoholicDrinks().drinks.orEmpty().mapNotNull { it?.toSummary() }

    // --- Mapping ---------------------------------------------------------------------------

    private fun RemoteMeal.toSummary(): RecipeSummary? {
        val id = idMeal ?: return null
        return RecipeSummary(id = id, type = RecipeType.MEAL, name = strMeal.orEmpty(), image = strMealThumb)
    }

    private fun RemoteDrink.toSummary(): RecipeSummary? {
        val id = idDrink ?: return null
        return RecipeSummary(id = id, type = RecipeType.DRINK, name = strDrink.orEmpty(), image = strDrinkThumb)
    }

    private fun RemoteDrink.isNonAlcoholic(): Boolean =
        !strAlcoholic.equals("Alcoholic", ignoreCase = true)

    private fun RemoteMeal.toRecipe(): Recipe {
        val pairs = listOf(
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
        )
        return Recipe(
            id = idMeal.orEmpty(),
            type = RecipeType.MEAL,
            name = strMeal.orEmpty(),
            image = strMealThumb,
            category = strCategory?.takeIf(String::isNotBlank),
            area = strArea?.takeIf { it.isNotBlank() && it != "Unknown" },
            drinkStyle = null,
            glass = null,
            tags = strTags.toTags(),
            ingredients = pairs.toIngredients(RecipeImages::mealIngredient),
            steps = strInstructions.toSteps(),
            videoUrl = strYoutube?.takeIf(String::isNotBlank),
            sourceUrl = strSource?.takeIf(String::isNotBlank)
        )
    }

    private fun RemoteDrink.toRecipe(): Recipe {
        val pairs = listOf(
            strIngredient1 to strMeasure1, strIngredient2 to strMeasure2,
            strIngredient3 to strMeasure3, strIngredient4 to strMeasure4,
            strIngredient5 to strMeasure5, strIngredient6 to strMeasure6,
            strIngredient7 to strMeasure7, strIngredient8 to strMeasure8,
            strIngredient9 to strMeasure9, strIngredient10 to strMeasure10,
            strIngredient11 to strMeasure11, strIngredient12 to strMeasure12,
            strIngredient13 to strMeasure13, strIngredient14 to strMeasure14,
            strIngredient15 to strMeasure15
        )
        return Recipe(
            id = idDrink.orEmpty(),
            type = RecipeType.DRINK,
            name = strDrink.orEmpty(),
            image = strDrinkThumb,
            category = strCategory?.takeIf(String::isNotBlank),
            area = null,
            drinkStyle = strAlcoholic?.takeIf(String::isNotBlank),
            glass = strGlass?.takeIf(String::isNotBlank),
            tags = strTags.toTags(),
            ingredients = pairs.toIngredients(RecipeImages::drinkIngredient),
            steps = strInstructions.toSteps(),
            videoUrl = strVideo?.takeIf(String::isNotBlank),
            sourceUrl = null
        )
    }

    private fun List<Pair<String?, String?>>.toIngredients(
        image: (String) -> String
    ): List<RecipeIngredient> = mapNotNull { (name, measure) ->
        val cleanName = name?.trim()?.takeIf(String::isNotEmpty) ?: return@mapNotNull null
        RecipeIngredient(
            name = cleanName,
            measure = measure?.trim().orEmpty(),
            image = image(cleanName)
        )
    }

    private fun String?.toTags(): List<String> =
        this?.split(",")?.map(String::trim)?.filter(String::isNotEmpty).orEmpty()

    // Instructions come as free text: sometimes one paragraph per line, sometimes
    // "STEP 1" / "1." headings, sometimes one long block. Normalise into readable steps.
    private fun String?.toSteps(): List<String> {
        if (this.isNullOrBlank()) return emptyList()
        val stepHeading = Regex("""^(step\s*\d+|\d+)[.:)]?$""", RegexOption.IGNORE_CASE)
        val leadingNumber = Regex("""^(step\s*\d+|\d+)[.:)]\s*""", RegexOption.IGNORE_CASE)

        val lines = this.split(Regex("""\r?\n"""))
            .map(String::trim)
            .filter { it.isNotEmpty() && !stepHeading.matches(it) }
            .map { it.replace(leadingNumber, "") }
            .filter(String::isNotEmpty)

        if (lines.size > 1) return lines

        // One long block: split into sentences and group them two at a time.
        val sentences = lines.firstOrNull().orEmpty()
            .split(Regex("""(?<=[.!?])\s+"""))
            .map(String::trim)
            .filter(String::isNotEmpty)
        return sentences.chunked(2).map { it.joinToString(" ") }
    }

    private companion object {
        val MEAT_CATEGORIES = listOf("Beef", "Chicken", "Lamb", "Pork", "Goat", "Seafood")

        // Used by "Surprise me" in vegetarian mode.
        val VEGETARIAN_CATEGORIES = listOf("Vegetarian", "Vegan", "Pasta", "Dessert", "Breakfast")

        // Matched against the start of each word ("Chicken Breast", "Minced Beef", "Anchovies"),
        // so "Champignon" or "Graham Crackers" don't trip over "ham". No "goat": goat cheese.
        val MEAT_KEYWORDS = listOf(
            "chicken", "beef", "pork", "lamb", "mutton", "veal", "venison", "duck", "turkey",
            "bacon", "ham", "sausage", "chorizo", "salami", "pancetta", "prosciutto", "pepperoni",
            "mince", "steak", "brisket", "oxtail", "liver", "lard", "suet",
            "fish", "salmon", "tuna", "cod", "haddock", "mackerel", "sardine", "trout", "anchov",
            "shellfish", "monkfish", "catfish", "swordfish",
            "prawn", "shrimp", "crab", "lobster", "mussel", "clam", "oyster", "scallop", "squid"
        )
    }
}
