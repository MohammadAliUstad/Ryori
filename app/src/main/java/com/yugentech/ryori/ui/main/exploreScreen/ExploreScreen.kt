package com.yugentech.ryori.ui.main.exploreScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Kitchen
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SortByAlpha
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeDefaults
import com.yugentech.ryori.ui.main.mainScreen.components.AnimatedSearchIcon
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.material.icons.rounded.SoupKitchen
import androidx.compose.material.icons.rounded.SetMeal
import androidx.compose.material.icons.rounded.RamenDining
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.BreakfastDining
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.border
import androidx.compose.animation.animateColorAsState
import androidx.activity.compose.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yugentech.ryori.api.model.domain.Area
import com.yugentech.ryori.api.model.domain.Category
import com.yugentech.ryori.api.model.domain.CuisineFlags
import com.yugentech.ryori.api.model.domain.DrinkCategories
import com.yugentech.ryori.api.model.domain.IngredientInfo
import com.yugentech.ryori.api.model.domain.RecipeFilter
import com.yugentech.ryori.api.model.domain.RecipeSummary
import com.yugentech.ryori.api.model.domain.RecipeType
import com.yugentech.ryori.api.viewmodel.ExploreUiState
import com.yugentech.ryori.api.viewmodel.ExploreViewModel
import com.yugentech.ryori.theme.service.HapticService
import com.yugentech.ryori.theme.tokens.corners
import com.yugentech.ryori.theme.tokens.spacing
import com.yugentech.ryori.ui.main.mainScreen.components.RecipeCard
import com.yugentech.ryori.ui.main.mainScreen.components.RecipeCardRow
import com.yugentech.ryori.ui.main.mainScreen.components.SectionHeader
import com.yugentech.ryori.ui.main.mainScreen.components.rowItemShape
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import androidx.compose.foundation.lazy.grid.items as gridItems

// Same setup as Quill's Discover screen: the content scrolls under a floating, frosted
// SearchBar (Haze blur + outline) that expands into full-screen search, with a surface
// gradient fading in behind it as the content scrolls up.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onRecipeClick: (RecipeType, String) -> Unit = { _, _ -> },
    onBrowse: (RecipeFilter, String) -> Unit = { _, _ -> },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: ExploreViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val dockedWidth = with(density) { windowInfo.containerSize.width.toDp() } - 32.dp
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    var searchExpanded by rememberSaveable { mutableStateOf(false) }
    val hazeState = remember { HazeState() }
    val listState = rememberLazyListState()

    val scrollAlpha by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex > 0) 1f
            else (listState.firstVisibleItemScrollOffset / 100f).coerceIn(0f, 1f)
        }
    }

    val closeSearch = {
        searchExpanded = false
        viewModel.clearSearch()
        focusManager.clearFocus()
    }

    BackHandler(enabled = searchExpanded) { closeSearch() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // --- The scrolling content (the haze source the search bar blurs) ---
        AnimatedContent(
            targetState = when {
                uiState.isLoading -> ExploreMode.Loading
                uiState.error != null -> ExploreMode.Error
                else -> ExploreMode.Browse
            },
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ExploreMode"
        ) { mode ->
            when (mode) {
                ExploreMode.Loading -> CenteredLoading()

                ExploreMode.Error -> ErrorState(
                    message = uiState.error ?: "Something went wrong",
                    onRetry = viewModel::load
                )

                ExploreMode.Browse -> BrowseSections(
                    uiState = uiState,
                    listState = listState,
                    contentPadding = PaddingValues(
                        top = statusBarHeight + 80.dp,
                        bottom = contentPadding.calculateBottomPadding() + MaterialTheme.spacing.s
                    ),
                    onSurprise = { type -> viewModel.surprise(type, onRecipeClick) },
                    onRecipeClick = { onRecipeClick(it.type, it.id) },
                    onBrowse = onBrowse,
                    modifier = Modifier.hazeSource(hazeState)
                )
            }
        }

        // --- The search bar area ---
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            val surfaceColor = MaterialTheme.colorScheme.surface
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(statusBarHeight + 110.dp)
                    .graphicsLayer { alpha = scrollAlpha }
                    .background(
                        Brush.verticalGradient(
                            0.0f to surfaceColor.copy(alpha = 0.9f),
                            0.4f to surfaceColor.copy(alpha = 0.7f),
                            0.7f to surfaceColor.copy(alpha = 0.30f),
                            1.0f to surfaceColor.copy(alpha = 0.0f)
                        )
                    )
            )

            val animatedContainerColor by animateColorAsState(
                targetValue = if (searchExpanded) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent,
                label = "container_color"
            )
            val animatedBorderColor by animateColorAsState(
                targetValue = if (searchExpanded) Color.Transparent
                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                label = "border_color"
            )

            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = uiState.query,
                        onQueryChange = viewModel::onQueryChange,
                        onSearch = { focusManager.clearFocus() },
                        expanded = searchExpanded,
                        onExpandedChange = { searchExpanded = it },
                        placeholder = { Text("Search dishes and drinks...") },
                        leadingIcon = {
                            IconButton(onClick = {
                                if (searchExpanded) closeSearch() else { searchExpanded = true }
                            }) {
                                AnimatedSearchIcon(isSearchActive = searchExpanded)
                            }
                        },
                        trailingIcon = {
                            if (uiState.query.isNotEmpty()) {
                                IconButton(onClick = viewModel::clearSearch) {
                                    Icon(Icons.Rounded.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier
                            .then(
                                if (!searchExpanded) {
                                    Modifier
                                        .clip(RoundedCornerShape(28.dp))
                                        .hazeEffect(
                                            state = hazeState,
                                            style = HazeDefaults.style(
                                                backgroundColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f),
                                                blurRadius = 20.dp,
                                                noiseFactor = 0.05f
                                            )
                                        )
                                } else Modifier
                            )
                            .border(
                                width = 1.dp,
                                color = animatedBorderColor,
                                shape = RoundedCornerShape(28.dp)
                            )
                    )
                },
                expanded = searchExpanded,
                onExpandedChange = { isActive -> if (isActive) { searchExpanded = true } else closeSearch() },
                modifier = Modifier.widthIn(min = dockedWidth),
                colors = SearchBarDefaults.colors(
                    containerColor = animatedContainerColor,
                    dividerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                windowInsets = SearchBarDefaults.windowInsets
            ) {
                if (uiState.query.isEmpty()) {
                    SearchSuggestions(
                        onSuggestionClick = { suggestion ->
                            viewModel.onQueryChange(suggestion)
                            focusManager.clearFocus()
                        }
                    )
                } else {
                    SearchResults(
                        uiState = uiState,
                        contentPadding = PaddingValues(
                            top = MaterialTheme.spacing.m,
                            bottom = contentPadding.calculateBottomPadding() + MaterialTheme.spacing.s
                        ),
                        onRecipeClick = { recipe ->
                            closeSearch()
                            onRecipeClick(recipe.type, recipe.id)
                        }
                    )
                }
            }
        }
    }
}

private enum class ExploreMode { Loading, Error, Browse }

// Shown in the expanded search before anything is typed, like Discover's suggestions.
private val searchSuggestions = listOf(
    "Chicken" to Icons.Rounded.SetMeal,
    "Pasta" to Icons.Rounded.RamenDining,
    "Curry" to Icons.Rounded.LocalFireDepartment,
    "Salad" to Icons.Rounded.Eco,
    "Soup" to Icons.Rounded.SoupKitchen,
    "Cake" to Icons.Rounded.Cake,
    "Pancakes" to Icons.Rounded.BreakfastDining,
    "Smoothie" to Icons.Rounded.LocalCafe
)

@Composable
private fun SearchSuggestions(onSuggestionClick: (String) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(MaterialTheme.spacing.m),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
    ) {
        items(searchSuggestions, key = { it.first }) { (name, icon) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(MaterialTheme.corners.medium))
                    .clickable { onSuggestionClick(name) }
                    .padding(vertical = 12.dp, horizontal = MaterialTheme.spacing.s)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(MaterialTheme.spacing.m))
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// --- Search -----------------------------------------------------------------------------------

@Composable
private fun SearchResults(
    uiState: ExploreUiState,
    contentPadding: PaddingValues,
    onRecipeClick: (RecipeSummary) -> Unit
) {
    when {
        uiState.isSearching && uiState.searchResults.isEmpty() -> CenteredLoading()

        uiState.searchResults.isEmpty() -> EmptyState(
            icon = Icons.Rounded.SearchOff,
            title = "No recipes found",
            message = "Try a dish like \"curry\" or a drink like \"smoothie\"."
        )

        else -> LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            contentPadding = PaddingValues(
                start = MaterialTheme.spacing.m,
                end = MaterialTheme.spacing.m,
                top = contentPadding.calculateTopPadding(),
                bottom = contentPadding.calculateBottomPadding()
            ),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
            modifier = Modifier.fillMaxSize()
        ) {
            gridItems(uiState.searchResults, key = { "${it.type}_${it.id}" }) { recipe ->
                RecipeCard(recipe = recipe, onClick = onRecipeClick)
            }
        }
    }
}

// --- Browse -----------------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BrowseSections(
    uiState: ExploreUiState,
    listState: LazyListState,
    contentPadding: PaddingValues,
    onSurprise: (RecipeType) -> Unit,
    onRecipeClick: (RecipeSummary) -> Unit,
    onBrowse: (RecipeFilter, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val horizontalPadding = PaddingValues(horizontal = MaterialTheme.spacing.m)

    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        modifier = modifier.fillMaxSize()
    ) {
        item {
            SurpriseRow(
                inProgress = uiState.surpriseInProgress,
                onSurprise = onSurprise,
                modifier = Modifier.padding(horizontalPadding)
            )
        }

        if (uiState.categories.isNotEmpty()) {
            item { SectionHeader(icon = Icons.Rounded.Category, title = "Categories") }
            item {
                LazyRow(
                    contentPadding = horizontalPadding,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
                ) {
                    items(uiState.categories, key = { it.id }) { category ->
                        CategoryTile(
                            category = category,
                            onClick = { category.name?.let { onBrowse(RecipeFilter.CATEGORY, it) } }
                        )
                    }
                }
            }
        }

        if (uiState.areas.isNotEmpty()) {
            item { SectionHeader(icon = Icons.Rounded.Public, title = "Cuisines") }
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                    modifier = Modifier.padding(horizontalPadding)
                ) {
                    uiState.areas.forEach { area ->
                        CuisineChip(area = area, onClick = { onBrowse(RecipeFilter.AREA, area.name) })
                    }
                }
            }
        }

        if (uiState.ingredients.isNotEmpty()) {
            item { SectionHeader(icon = Icons.Rounded.Kitchen, title = "Cook With") }
            item {
                LazyRow(
                    contentPadding = horizontalPadding,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
                ) {
                    items(uiState.ingredients, key = { it.name }) { ingredient ->
                        IngredientBubble(
                            ingredient = ingredient,
                            onClick = { onBrowse(RecipeFilter.INGREDIENT, ingredient.name) }
                        )
                    }
                }
            }
        }

        if (uiState.drinks.isNotEmpty()) {
            item { SectionHeader(icon = Icons.Rounded.LocalCafe, title = "Drinks & Mocktails") }
            item {
                LazyRow(
                    contentPadding = horizontalPadding,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
                ) {
                    items(DrinkCategories.all, key = { it.first }) { (category, label) ->
                        FilterPill(
                            label = label,
                            onClick = { onBrowse(RecipeFilter.DRINK_CATEGORY, category) }
                        )
                    }
                }
            }
            item {
                RecipeCardRow(
                    recipes = uiState.drinks,
                    onClick = onRecipeClick,
                    contentPadding = horizontalPadding,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.s),
                    trailing = { SeeAllTile(onClick = { onBrowse(RecipeFilter.DRINKS, "all") }) }
                )
            }
        }

        item { SectionHeader(icon = Icons.Rounded.SortByAlpha, title = "Browse A-Z") }
        item {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                modifier = Modifier.padding(horizontalPadding)
            ) {
                ('A'..'Z').forEach { letter ->
                    LetterButton(
                        letter = letter,
                        onClick = { onBrowse(RecipeFilter.LETTER, letter.toString()) }
                    )
                }
            }
        }
    }
}

// Two grouped cards (primary / tertiary): Quill's glance-card treatment laid out as a row.
@Composable
private fun SurpriseRow(
    inProgress: RecipeType?,
    onSurprise: (RecipeType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        SurpriseCard(
            title = "Surprise dish",
            subtitle = "Random recipe",
            icon = Icons.Rounded.Restaurant,
            loading = inProgress == RecipeType.MEAL,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = rowItemShape(index = 0, count = 2),
            onClick = { onSurprise(RecipeType.MEAL) },
            modifier = Modifier.weight(1f)
        )
        SurpriseCard(
            title = "Surprise drink",
            subtitle = "Non-alcoholic",
            icon = Icons.Rounded.LocalCafe,
            loading = inProgress == RecipeType.DRINK,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            shape = rowItemShape(index = 1, count = 2),
            onClick = { onSurprise(RecipeType.DRINK) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SurpriseCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    loading: Boolean,
    containerColor: Color,
    contentColor: Color,
    shape: Shape,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = koinInject<HapticService>()
    val view = LocalView.current

    Card(
        onClick = {
            haptic.performHaptic(view)
            onClick()
        },
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.m)) {
            Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                if (loading) {
                    CircularProgressIndicator(
                        color = contentColor,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Icon(imageVector = Icons.Rounded.Shuffle, contentDescription = null)
                }
            }
            Spacer(Modifier.height(MaterialTheme.spacing.m))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(MaterialTheme.spacing.xs))
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun CategoryTile(category: Category, onClick: () -> Unit) {
    val haptic = koinInject<HapticService>()
    val view = LocalView.current

    Card(
        onClick = {
            haptic.performHaptic(view)
            onClick()
        },
        shape = RoundedCornerShape(MaterialTheme.corners.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.width(128.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.s)
        ) {
            AsyncImage(
                model = category.image,
                contentDescription = category.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.3f)
            )
            Text(
                text = category.name.orEmpty(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = MaterialTheme.spacing.xs)
            )
        }
    }
}

@Composable
private fun CuisineChip(area: Area, onClick: () -> Unit) {
    FilterPill(label = "${CuisineFlags.flag(area.name)}  ${area.name}", onClick = onClick)
}

@Composable
private fun FilterPill(label: String, onClick: () -> Unit) {
    val haptic = koinInject<HapticService>()
    val view = LocalView.current

    Surface(
        onClick = {
            haptic.performHaptic(view)
            onClick()
        },
        shape = RoundedCornerShape(MaterialTheme.corners.pill),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun IngredientBubble(ingredient: IngredientInfo, onClick: () -> Unit) {
    val haptic = koinInject<HapticService>()
    val view = LocalView.current

    // The tap area is rounded, so its content is inset from the edges: without the inner
    // padding the rounded corners clipped the ends of longer ingredient names.
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(84.dp)
            .clip(RoundedCornerShape(MaterialTheme.corners.medium))
            .clickable {
                haptic.performHaptic(view)
                onClick()
            }
            .padding(horizontal = MaterialTheme.spacing.xs, vertical = MaterialTheme.spacing.xs)
    ) {
        AsyncImage(
            model = ingredient.image,
            contentDescription = ingredient.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(10.dp)
        )
        Text(
            text = ingredient.name,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = MaterialTheme.spacing.xs)
        )
    }
}

@Composable
private fun SeeAllTile(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(MaterialTheme.corners.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .width(150.dp)
            .aspectRatio(1f)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "See all drinks",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun LetterButton(letter: Char, onClick: () -> Unit) {
    val haptic = koinInject<HapticService>()
    val view = LocalView.current

    Surface(
        onClick = {
            haptic.performHaptic(view)
            onClick()
        },
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.size(44.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = letter.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// --- Shared states ----------------------------------------------------------------------------

@Composable
private fun CenteredLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.xl)
    ) {
        Text(
            text = "Couldn't load Explore",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(MaterialTheme.spacing.xs))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(MaterialTheme.spacing.m))
        Button(onClick = onRetry) { Text("Try again") }
    }
}

@Composable
private fun EmptyState(icon: ImageVector, title: String, message: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.xl)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(MaterialTheme.spacing.m))
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(MaterialTheme.spacing.xs))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
