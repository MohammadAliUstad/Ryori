@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.yugentech.ryori.ui.main.exploreScreen

import com.yugentech.ryori.R
import androidx.compose.ui.res.stringResource
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SortByAlpha
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import kotlinx.coroutines.delay
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
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yugentech.ryori.api.error.AppError
import kotlinx.coroutines.launch
import com.yugentech.ryori.theme.tokens.dimensions.AppAnimations
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
import com.yugentech.ryori.theme.tokens.corners
import com.yugentech.ryori.theme.tokens.spacing
import com.yugentech.ryori.ui.main.mainScreen.components.ErrorState
import com.yugentech.ryori.ui.main.mainScreen.components.RecipeCard
import com.yugentech.ryori.ui.main.mainScreen.components.RecipeCardRow
import com.yugentech.ryori.ui.main.mainScreen.components.RecipeCardRowPlaceholder
import com.yugentech.ryori.ui.main.mainScreen.components.SectionHeader
import com.yugentech.ryori.ui.main.mainScreen.components.ToastMessage
import com.yugentech.ryori.ui.main.mainScreen.components.loadingPulse
import com.yugentech.ryori.ui.main.mainScreen.components.rowItemShape
import org.koin.androidx.compose.koinViewModel
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
            // The sections are on screen from the first frame, with skeletons standing in for
            // anything still loading. Only a load that brings back nothing swaps to the error
            // screen. Paired with the error so the error screen keeps its message while it fades
            // out after "Try again" (the view model clears the error straight away).
            targetState = (if (uiState.error != null) ExploreMode.Error else ExploreMode.Browse) to uiState.error,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ExploreMode"
        ) { (mode, shownError) ->
            when (mode) {
                ExploreMode.Error -> ErrorState(
                    error = shownError ?: AppError.UNKNOWN,
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
                        placeholder = { Text(stringResource(R.string.explore_search_hint)) },
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
                                    Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.clear))
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
                SearchContent(
                    uiState = uiState,
                    contentPadding = PaddingValues(
                        top = MaterialTheme.spacing.m,
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + MaterialTheme.spacing.s
                    ),
                    onRecipeClick = { recipe ->
                        closeSearch()
                        onRecipeClick(recipe.type, recipe.id)
                    },
                    onRetry = viewModel::retrySearch
                )
            }
        }

        // Problems that don't take over the screen: a failed surprise, or sections that
        // couldn't load. Sits just under the search bar.
        ToastMessage(
            message = uiState.notice?.short?.let { stringResource(it) },
            onDismiss = viewModel::dismissNotice,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = statusBarHeight + 80.dp)
                .zIndex(2f)
        )
    }
}

private enum class ExploreMode { Error, Browse }

// --- Search -----------------------------------------------------------------------------------

// What the expanded search shows. Whatever is on screen fades out before the next one fades in,
// so old recipes never linger under the spinner, a state, or the new results.
private sealed interface SearchView {
    data object Prompt : SearchView
    data object Loading : SearchView
    data class Failed(val error: AppError) : SearchView
    data object NoResults : SearchView
    data class Results(val recipes: List<RecipeSummary>) : SearchView
}

private fun ExploreUiState.searchView(): SearchView {
    val error = searchError
    return when {
        // Blank, like empty: the view model doesn't search spaces
        query.isBlank() -> SearchView.Prompt
        isSearching -> SearchView.Loading
        // Offline or server trouble: say so rather than "No recipes found".
        error != null -> SearchView.Failed(error)
        searchResults.isEmpty() -> SearchView.NoResults
        else -> SearchView.Results(searchResults)
    }
}

@Composable
private fun SearchContent(
    uiState: ExploreUiState,
    contentPadding: PaddingValues,
    onRecipeClick: (RecipeSummary) -> Unit,
    onRetry: () -> Unit
) {
    AnimatedContent(
        targetState = uiState.searchView(),
        transitionSpec = {
            fadeIn(
                tween(
                    durationMillis = AppAnimations.Durations.Smooth,
                    delayMillis = AppAnimations.Durations.Rapid,
                    easing = AppAnimations.Easings.Decelerate
                )
            ) togetherWith fadeOut(
                tween(durationMillis = AppAnimations.Durations.Rapid, easing = AppAnimations.Easings.Accelerate)
            )
        },
        label = "SearchContent",
        modifier = Modifier.fillMaxSize()
    ) { view ->
        when (view) {
            SearchView.Prompt -> SearchStateLayout(
                icon = { StateIcon(Icons.Rounded.RestaurantMenu) },
                title = stringResource(R.string.explore_prompt_title),
                message = stringResource(R.string.explore_prompt_body)
            )

            // In the icon's spot, with blank text keeping the layout the same height, so the
            // spinner sits exactly where the prompt's and no results' icons do.
            SearchView.Loading -> SearchStateLayout(
                icon = { CircularWavyProgressIndicator() },
                title = "",
                message = ""
            )

            is SearchView.Failed -> ErrorState(error = view.error, onRetry = onRetry)

            SearchView.NoResults -> SearchStateLayout(
                icon = { StateIcon(Icons.Rounded.SearchOff) },
                title = stringResource(R.string.explore_no_results_title),
                message = stringResource(R.string.explore_no_results_message)
            )

            is SearchView.Results -> LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(
                    start = MaterialTheme.spacing.m,
                    end = MaterialTheme.spacing.m,
                    top = contentPadding.calculateTopPadding(),
                    bottom = contentPadding.calculateBottomPadding()
                ),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.none),
                modifier = Modifier.fillMaxSize()
            ) {
                gridItems(view.recipes, key = { "${it.type}_${it.id}" }) { recipe ->
                    RecipeCard(recipe = recipe, onClick = onRecipeClick)
                }
            }
        }
    }
}

@Composable
private fun StateIcon(icon: ImageVector) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
        modifier = Modifier.size(64.dp)
    )
}

// The one layout for the expanded search's states (the prompt before anything is typed, as in
// Quill's Gutenberg search, no results, and loading), so they sit at the same spot: a 64dp
// icon, a title and a two-line message, a little above centre. On appearing, the icon settles in from a
// little smaller and the text rises a few dp just behind it.
@Composable
private fun SearchStateLayout(title: String, message: String, icon: @Composable () -> Unit) {
    val iconIn = remember { Animatable(0f) }
    val textIn = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        val spec = tween<Float>(
            durationMillis = AppAnimations.Durations.Emphasized,
            easing = AppAnimations.Easings.EmphasizedDecelerate
        )
        launch { iconIn.animateTo(1f, spec) }
        delay(AppAnimations.Durations.Rapid.toLong())
        textIn.animateTo(1f, spec)
    }
    val textModifier = Modifier.graphicsLayer {
        alpha = textIn.value
        translationY = (1f - textIn.value) * 8.dp.toPx()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Spacer(Modifier.weight(0.3f))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer {
                    val scale = 0.85f + 0.15f * iconIn.value
                    alpha = iconIn.value
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            icon()
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = textModifier
        )
        Spacer(Modifier.height(MaterialTheme.spacing.s))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            minLines = 2,
            modifier = textModifier
        )
        Spacer(Modifier.weight(0.7f))
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
    // One pulse for every skeleton, so they all breathe together.
    // Outside the LazyColumn, so it outlives the cuisines item scrolling off screen. It starts at
    // the cuisines already loaded: coming back to the tab, those are shown as they are, and only
    // a batch that arrives while you're here pops in.
    val cuisinesShown = remember { ShownCount(uiState.areas.size) }

    // What needs no network (the surprise cards, the drink categories, A-Z) shows straight away.
    // Each loaded section shows a skeleton the size of its content until its data is in, then
    // fades to it; a section whose request failed is left out (the toast says why).
    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        modifier = modifier.fillMaxSize()
    ) {
        item {
            // The cards keep their icons while a surprise is fetched (it only takes a moment): the
            // screen just navigates once it's found, and ExploreViewModel ignores repeat taps
            SurpriseRow(
                onSurprise = onSurprise,
                modifier = Modifier.padding(horizontalPadding)
            )
        }

        if (uiState.categoriesLoading || uiState.categories.isNotEmpty()) {
            item { SectionHeader(icon = Icons.Rounded.Category, title = stringResource(R.string.explore_categories)) }
            item {
                SectionCrossfade(loading = uiState.categoriesLoading, label = "categories") { showPlaceholder ->
                    if (showPlaceholder) {
                        CategoryRowPlaceholder(contentPadding = horizontalPadding)
                    } else {
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
            }
        }

        if (uiState.areasLoading || uiState.areas.isNotEmpty()) {
            item { SectionHeader(icon = Icons.Rounded.Public, title = stringResource(R.string.explore_cuisines)) }
            item {
                // Cuisines come in batches on a cold cache: the skeleton only stands in until the
                // first batch is found, then each new batch fades in at the end, with a few
                // skeleton pills after them until the last check is in.
                SectionCrossfade(
                    loading = uiState.areasLoading && uiState.areas.isEmpty(),
                    label = "cuisines"
                ) { showPlaceholder ->
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                        itemVerticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontalPadding)
                            .animateContentSize()
                    ) {
                        if (showPlaceholder) {
                            cuisinePlaceholderWidths.forEach { width -> PillPlaceholder(width = width) }
                        } else {
                            // Chips past the ones already shown are the new batch; they pop in one
                            // after another. Chips shown before (e.g. scrolled back into view) don't.
                            val firstNew = cuisinesShown.count
                            SideEffect { cuisinesShown.count = uiState.areas.size }
                            uiState.areas.forEachIndexed { index, area ->
                                key(area.name) {
                                    PopIn(
                                        animate = index >= firstNew,
                                        delayMillis = (index - firstNew).coerceIn(0, CUISINE_STAGGER_MAX_STEPS) *
                                            CUISINE_STAGGER_MILLIS
                                    ) {
                                        CuisineChip(area = area, onClick = { onBrowse(RecipeFilter.AREA, area.name) })
                                    }
                                }
                            }
                            if (uiState.areasLoading) {
                                cuisinePlaceholderWidths.take(CUISINE_TRAILING_PLACEHOLDER_COUNT).forEach { width ->
                                    PillPlaceholder(width = width)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (uiState.ingredientsLoading || uiState.ingredients.isNotEmpty()) {
            item { SectionHeader(icon = Icons.Rounded.Kitchen, title = stringResource(R.string.explore_cook_with)) }
            item {
                SectionCrossfade(loading = uiState.ingredientsLoading, label = "ingredients") { showPlaceholder ->
                    if (showPlaceholder) {
                        IngredientRowPlaceholder(contentPadding = horizontalPadding)
                    } else {
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
            }
        }

        // The drink categories are fixed, so they show straight away; only the drinks row loads.
        item { SectionHeader(icon = Icons.Rounded.LocalCafe, title = stringResource(R.string.list_title_drinks)) }
        item {
            LazyRow(
                contentPadding = horizontalPadding,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
            ) {
                items(DrinkCategories.all, key = { it.first }) { (category, label) ->
                    FilterPill(
                        label = stringResource(label),
                        onClick = { onBrowse(RecipeFilter.DRINK_CATEGORY, category) }
                    )
                }
            }
        }
        if (uiState.drinksLoading || uiState.drinks.isNotEmpty()) {
            item {
                SectionCrossfade(loading = uiState.drinksLoading, label = "drinks") { showPlaceholder ->
                    if (showPlaceholder) {
                        RecipeCardRowPlaceholder(
                            contentPadding = horizontalPadding,
                            modifier = Modifier.padding(top = MaterialTheme.spacing.s)
                        )
                    } else {
                        RecipeCardRow(
                            recipes = uiState.drinks,
                            onClick = onRecipeClick,
                            contentPadding = horizontalPadding,
                            modifier = Modifier.padding(top = MaterialTheme.spacing.s),
                            trailing = { SeeAllTile(onClick = { onBrowse(RecipeFilter.DRINKS, "all") }) }
                        )
                    }
                }
            }
        }

        item { SectionHeader(icon = Icons.Rounded.SortByAlpha, title = stringResource(R.string.explore_browse_az)) }
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
    onSurprise: (RecipeType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        SurpriseCard(
            title = stringResource(R.string.explore_surprise_dish),
            subtitle = stringResource(R.string.explore_surprise_dish_subtitle),
            icon = Icons.Rounded.Restaurant,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = rowItemShape(index = 0, count = 2),
            onClick = { onSurprise(RecipeType.MEAL) },
            modifier = Modifier.weight(1f)
        )
        SurpriseCard(
            title = stringResource(R.string.explore_surprise_drink),
            subtitle = stringResource(R.string.explore_surprise_drink_subtitle),
            icon = Icons.Rounded.LocalCafe,
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
    containerColor: Color,
    contentColor: Color,
    shape: Shape,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Card(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
            onClick()
        },
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.m)) {
            Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Rounded.Shuffle, contentDescription = null)
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
    val haptic = LocalHapticFeedback.current

    Card(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
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
    val haptic = LocalHapticFeedback.current

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
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
    val haptic = LocalHapticFeedback.current

    // The tap area is rounded, so its content is inset from the edges: without the inner
    // padding the rounded corners clipped the ends of longer ingredient names.
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(84.dp)
            .clip(RoundedCornerShape(MaterialTheme.corners.medium))
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
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
                text = stringResource(R.string.explore_see_all_drinks),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun LetterButton(letter: Char, onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
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

// --- Loading skeletons ------------------------------------------------------------------------

// How long a skeleton takes to fade into its loaded content (the same as Home's).
private const val EXPLORE_REVEAL_MILLIS = 400

// Fades a section from its skeleton (content(true)) to its loaded content (content(false)).
@Composable
private fun SectionCrossfade(
    loading: Boolean,
    label: String,
    content: @Composable (showPlaceholder: Boolean) -> Unit
) {
    Crossfade(
        targetState = loading,
        animationSpec = tween(EXPLORE_REVEAL_MILLIS),
        label = label
    ) { showPlaceholder -> content(showPlaceholder) }
}

// How many cuisine chips have been shown, to tell a newly arrived batch from the rest. A plain
// holder rather than state: it's only read to pick the starting animation, never to redraw.
private class ShownCount(var count: Int = 0)

// Fades and springs its content up from slightly smaller after [delayMillis], e.g. a cuisine
// chip that just arrived. It takes its full space from the start, so the row doesn't reflow
// as it appears.
@Composable
private fun PopIn(animate: Boolean, delayMillis: Int, content: @Composable () -> Unit) {
    val progress = remember { Animatable(if (animate) 0f else 1f) }
    val spec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    LaunchedEffect(Unit) {
        if (progress.value < 1f) {
            delay(delayMillis.toLong())
            progress.animateTo(1f, spec)
        }
    }
    Box(
        modifier = Modifier.graphicsLayer {
            // The expressive spring overshoots a little: a bounce on the scale, none on the alpha.
            alpha = progress.value.coerceIn(0f, 1f)
            val scale = 0.8f + 0.2f * progress.value
            scaleX = scale
            scaleY = scale
        }
    ) { content() }
}

// Same as the categories LazyRow: CategoryTile's 128dp card, its padding, the 1.3 image and one
// line of label, with the row's spacing. Not scrollable while loading.
@Composable
private fun CategoryRowPlaceholder(contentPadding: PaddingValues) {
    LazyRow(
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
        userScrollEnabled = false
    ) {
        items(CATEGORY_PLACEHOLDER_COUNT) {
            Card(
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.3f)
                            .clip(RoundedCornerShape(MaterialTheme.corners.medium))
                            .loadingPulse()
                    )
                    // An empty Text in the label's style takes exactly one label line of height.
                    Text(
                        text = "",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(top = MaterialTheme.spacing.xs)
                            .fillMaxWidth(0.6f)
                            .clip(RoundedCornerShape(MaterialTheme.corners.small))
                            .loadingPulse()
                    )
                }
            }
        }
    }
}

// The same height as a CuisineChip (one labelLarge line plus its 8dp top and bottom padding), at
// a given width. The line holds an invisible flag: the emoji makes a chip's line taller than
// plain (or empty) text, which left the skeletons sitting higher than the chips beside them.
@Composable
private fun PillPlaceholder(width: Dp) {
    Text(
        text = CuisineFlags.flag(PLACEHOLDER_FLAG_AREA),
        color = Color.Transparent,
        maxLines = 1,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .clip(RoundedCornerShape(MaterialTheme.corners.pill))
            .loadingPulse()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .width(width)
    )
}

// Same as the ingredients LazyRow: IngredientBubble's 84dp column, 68dp circle and one line of
// label, with the row's spacing.
@Composable
private fun IngredientRowPlaceholder(contentPadding: PaddingValues) {
    LazyRow(
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m),
        userScrollEnabled = false
    ) {
        items(INGREDIENT_PLACEHOLDER_COUNT) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(84.dp)
                    .padding(horizontal = MaterialTheme.spacing.xs, vertical = MaterialTheme.spacing.xs)
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .loadingPulse()
                )
                Text(
                    text = "",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(top = MaterialTheme.spacing.xs)
                        .fillMaxWidth(0.7f)
                        .clip(RoundedCornerShape(MaterialTheme.corners.small))
                        .loadingPulse()
                )
            }
        }
    }
}

// Enough tiles and bubbles to fill the row's width on any phone.
private const val CATEGORY_PLACEHOLDER_COUNT = 5
private const val INGREDIENT_PLACEHOLDER_COUNT = 6

// Pill widths for the cuisines skeleton (text width, inside the pill's padding), mixed like
// real cuisine names. The real list is longer, so the section grows a little once it's in.
private val cuisinePlaceholderWidths = listOf(72, 96, 64, 88, 104, 76, 92, 60, 84, 100, 68, 80).map { it.dp }

// Skeleton pills after the cuisines found so far, while the rest are still being checked.
private const val CUISINE_TRAILING_PLACEHOLDER_COUNT = 3

// Any cuisine with a flag, measured by the skeleton pills to match the chips' height.
private const val PLACEHOLDER_FLAG_AREA = "Italian"

// The gap between chips of a new batch popping in, capped so a whole list landing at once
// (a warm cache) still finishes quickly.
private const val CUISINE_STAGGER_MILLIS = 35
private const val CUISINE_STAGGER_MAX_STEPS = 10
