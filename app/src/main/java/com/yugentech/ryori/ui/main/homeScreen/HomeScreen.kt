@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.yugentech.ryori.ui.main.homeScreen

import com.yugentech.ryori.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withTimeoutOrNull
import com.yugentech.ryori.api.error.AppError
import com.yugentech.ryori.api.model.domain.Category
import com.yugentech.ryori.api.model.domain.CuisineFlags
import com.yugentech.ryori.api.model.domain.Recipe
import com.yugentech.ryori.api.model.domain.RecipeFilter
import com.yugentech.ryori.api.model.domain.RecipeSummary
import com.yugentech.ryori.api.model.domain.RecipeType
import com.yugentech.ryori.api.viewmodel.HomeUiState
import com.yugentech.ryori.api.viewmodel.HomeViewModel
import com.yugentech.ryori.theme.tokens.corners
import com.yugentech.ryori.theme.tokens.dimensions.AppAnimations
import com.yugentech.ryori.theme.tokens.spacing
import com.yugentech.ryori.ui.main.mainScreen.components.ErrorState
import com.yugentech.ryori.ui.main.mainScreen.components.ParallaxBackground
import com.yugentech.ryori.ui.main.mainScreen.components.RecipeCardRow
import com.yugentech.ryori.ui.main.mainScreen.components.RecipeCardRowPlaceholder
import com.yugentech.ryori.ui.main.mainScreen.components.ToastMessage
import com.yugentech.ryori.ui.main.mainScreen.components.loadingPulse
import com.yugentech.ryori.ui.main.mainScreen.components.ScrimTopAppBar
import com.yugentech.ryori.ui.main.mainScreen.components.rowItemShape
import java.time.LocalTime
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRecipeClick: (RecipeType, String) -> Unit = { _, _ -> },
    onBrowse: (RecipeFilter, String) -> Unit = { _, _ -> },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    val fadeThrough = AppAnimations.Transitions.rememberExpressiveFadeThrough()

    val parallaxScrollOffset by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) listState.firstVisibleItemScrollOffset else 10000
        }
    }
    // The featured recipe currently on screen in the carousel.
    var parallaxImage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            // Transparent over the blurred parallax photo; a scrim fades in once content scrolls
            // under it. The same bar as Settings here and Sessions' main pages
            ScrimTopAppBar(
                title = {
                    Text(
                        text = "Ryori",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        val listPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding() + MaterialTheme.spacing.s
        )

        Box(modifier = Modifier.fillMaxSize()) {
            // Drawn from the very top (under the transparent bar) so the photo shows behind it.
            AnimatedVisibility(
                visible = !uiState.isLoading && parallaxImage != null,
                enter = fadeIn(tween(HOME_REVEAL_MILLIS)),
                exit = fadeOut(tween(HOME_REVEAL_MILLIS))
            ) {
                ParallaxBackground(
                    imageUrl = parallaxImage,
                    scrollOffset = parallaxScrollOffset,
                    headerHeight = 440.dp
                )
            }

            // The real layout is on screen from the first frame, with placeholders standing in
            // for anything still loading. Only a load that brings back nothing swaps to the
            // error screen.
            AnimatedContent(
                // The target carries the error itself, so the error screen keeps its message while
                // it fades out after "Try again" (the view model clears the error straight away).
                targetState = if (!uiState.isLoading && uiState.featured.isEmpty()) {
                    uiState.error ?: AppError.UNKNOWN
                } else {
                    null
                },
                transitionSpec = { fadeThrough() },
                label = "HomeError"
            ) { shownError ->
                if (shownError != null) {
                    ErrorState(
                        error = shownError,
                        onRetry = viewModel::load
                    )
                } else {
                    HomeContent(
                        uiState = uiState,
                        listState = listState,
                        contentPadding = listPadding,
                        onFeaturedImageChange = { parallaxImage = it },
                        onRecipeClick = onRecipeClick,
                        onBrowse = onBrowse,
                        onShuffleFeatured = viewModel::refreshFeatured,
                        onSurprise = { viewModel.surprise(onRecipeClick) }
                    )
                }
            }

            // Problems that don't take over the screen: a failed shuffle or surprise, or rows
            // that couldn't load.
            ToastMessage(
                message = uiState.notice?.short?.let { stringResource(it) },
                onDismiss = viewModel::dismissNotice,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = innerPadding.calculateTopPadding() + MaterialTheme.spacing.s)
            )
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    listState: LazyListState,
    contentPadding: PaddingValues,
    onFeaturedImageChange: (String?) -> Unit,
    onRecipeClick: (RecipeType, String) -> Unit,
    onBrowse: (RecipeFilter, String) -> Unit,
    onShuffleFeatured: () -> Unit,
    onSurprise: () -> Unit
) {
    val openSummary: (RecipeSummary) -> Unit = { onRecipeClick(it.type, it.id) }
    val rowPadding = PaddingValues(horizontal = MaterialTheme.spacing.m)
    val loading = uiState.isLoading
    val haptic = LocalHapticFeedback.current

    // While loading, every section shows its real header straight away and a placeholder the
    // exact size of its content, so nothing moves when the data arrives. Each placeholder
    // crossfades into its content rather than snapping.
    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        modifier = Modifier.fillMaxSize()
    ) {
        // Greeting + featured carousel are one list item on purpose (same as Quill's Discover
        // hero): the parallax backdrop fades using the first item's scroll offset, so the first
        // item has to be taller than the backdrop or the photo cuts out as soon as it scrolls by.
        item(key = "hero") {
            Column {
                Greeting()
                HomeSectionHeader(
                    icon = Icons.Rounded.Star,
                    title = stringResource(R.string.home_featured_today),
                    action = {
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                onShuffleFeatured()
                            },
                            enabled = !loading && !uiState.isRefreshingFeatured
                        ) {
                            if (uiState.isRefreshingFeatured) {
                                // Thinner stroke than the default so the wave still reads at this small size
                                val wavyStroke = Stroke(width = with(LocalDensity.current) { 2.dp.toPx() }, cap = StrokeCap.Round)
                                CircularWavyProgressIndicator(
                                    stroke = wavyStroke,
                                    trackStroke = wavyStroke,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Icon(Icons.Rounded.Refresh, contentDescription = stringResource(R.string.home_new_picks))
                            }
                        }
                    }
                )
                LoadingCrossfade(loading = loading, label = "featured") { showPlaceholder ->
                    if (showPlaceholder) {
                        FeaturedCarouselPlaceholder()
                    } else {
                        FeaturedCarousel(
                            recipes = uiState.featured,
                            onClick = { onRecipeClick(it.type, it.id) },
                            onCurrentImageChange = onFeaturedImageChange
                        )
                    }
                }
            }
        }

        // Quick picks need no data, so they're usable while everything else loads.
        item(key = "quick_picks") {
            // Surprise me keeps its icon while it fetches (it only takes a moment): the screen just
            // navigates once the recipe is found, and HomeViewModel ignores repeat taps meanwhile
            QuickPicks(
                onSurprise = onSurprise,
                onBrowse = onBrowse,
                modifier = Modifier.padding(
                    start = MaterialTheme.spacing.m,
                    end = MaterialTheme.spacing.m,
                    top = MaterialTheme.spacing.l
                )
            )
        }

        // The cuisine and category of the day are picked at random, so their names only
        // arrive with the data; until then the headers show a general title.
        if (loading || uiState.cuisineSpotlight != null) {
            val area = uiState.cuisineSpotlight
            item(key = "cuisine_header") {
                HomeSectionHeader(
                    icon = Icons.Rounded.Public,
                    title = if (area != null) stringResource(R.string.home_taste_of, "${CuisineFlags.flag(area)} $area") else stringResource(R.string.home_taste_of_world),
                    action = {
                        SeeAllButton(enabled = area != null) {
                            area?.let { onBrowse(RecipeFilter.AREA, it) }
                        }
                    }
                )
            }
            item(key = "cuisine_row") {
                LoadingCrossfade(loading = loading, label = "cuisine_row") { showPlaceholder ->
                    if (showPlaceholder) {
                        RecipeCardRowPlaceholder(contentPadding = rowPadding)
                    } else {
                        RecipeCardRow(recipes = uiState.cuisineRecipes, onClick = openSummary, contentPadding = rowPadding)
                    }
                }
            }
        }

        if (loading || uiState.categorySpotlight != null) {
            val category = uiState.categorySpotlight
            item(key = "category_header") {
                HomeSectionHeader(
                    icon = Icons.Rounded.RestaurantMenu,
                    title = if (category != null) stringResource(R.string.home_category_favourites, category) else stringResource(R.string.home_todays_favourites),
                    action = {
                        SeeAllButton(enabled = category != null) {
                            category?.let { onBrowse(RecipeFilter.CATEGORY, it) }
                        }
                    }
                )
            }
            item(key = "category_row") {
                LoadingCrossfade(loading = loading, label = "category_row") { showPlaceholder ->
                    if (showPlaceholder) {
                        RecipeCardRowPlaceholder(contentPadding = rowPadding)
                    } else {
                        RecipeCardRow(recipes = uiState.categoryRecipes, onClick = openSummary, contentPadding = rowPadding)
                    }
                }
            }
        }

        if (loading || uiState.drinks.isNotEmpty()) {
            item(key = "drinks_header") {
                HomeSectionHeader(
                    icon = Icons.Rounded.LocalCafe,
                    title = stringResource(R.string.home_something_to_sip),
                    action = { SeeAllButton(enabled = !loading) { onBrowse(RecipeFilter.DRINKS, "all") } }
                )
            }
            item(key = "drinks_row") {
                LoadingCrossfade(loading = loading, label = "drinks_row") { showPlaceholder ->
                    if (showPlaceholder) {
                        RecipeCardRowPlaceholder(contentPadding = rowPadding)
                    } else {
                        RecipeCardRow(recipes = uiState.drinks, onClick = openSummary, contentPadding = rowPadding)
                    }
                }
            }
        }

        if (loading || uiState.categories.isNotEmpty()) {
            item(key = "categories_header") {
                HomeSectionHeader(icon = Icons.Rounded.Category, title = stringResource(R.string.home_browse_by_category))
            }
            // Same 3-wide rows either way; while loading, placeholder tiles fill them.
            val rows: List<List<Category?>> = if (loading) {
                List(CATEGORY_PLACEHOLDER_ROWS) { List(3) { null } }
            } else {
                uiState.categories.chunked(3)
            }
            rows.forEachIndexed { rowIndex, row ->
                item(key = "category_grid_$rowIndex") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                        modifier = Modifier
                            // Rows beyond the placeholder ones fade in instead of popping.
                            .animateItem(
                                fadeInSpec = tween(HOME_REVEAL_MILLIS),
                                placementSpec = null,
                                fadeOutSpec = tween(HOME_REVEAL_MILLIS)
                            )
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.m)
                            .padding(bottom = if (rowIndex < rows.lastIndex) MaterialTheme.spacing.s else 0.dp)
                    ) {
                        row.forEach { category ->
                            Crossfade(
                                targetState = category,
                                animationSpec = tween(HOME_REVEAL_MILLIS),
                                label = "category_tile",
                                modifier = Modifier.weight(1f)
                            ) { tile ->
                                if (tile == null) {
                                    CategoryTilePlaceholder()
                                } else {
                                    CategoryTile(
                                        category = tile,
                                        onClick = { tile.name?.let { onBrowse(RecipeFilter.CATEGORY, it) } }
                                    )
                                }
                            }
                        }
                        // Keep tiles the same width on a short last row.
                        repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

// Rows of placeholder category tiles shown while loading.
private const val CATEGORY_PLACEHOLDER_ROWS = 3

// How long a skeleton takes to fade into its loaded content.
private const val HOME_REVEAL_MILLIS = 400

// Fades a section from its placeholder (content(true)) to its loaded content (content(false)).
// Both are the same size, so the crossfade never moves anything around it.
@Composable
private fun LoadingCrossfade(
    loading: Boolean,
    label: String,
    content: @Composable (showPlaceholder: Boolean) -> Unit
) {
    Crossfade(
        targetState = loading,
        animationSpec = tween(HOME_REVEAL_MILLIS),
        label = label
    ) { showPlaceholder -> content(showPlaceholder) }
}

// --- Greeting & headers -----------------------------------------------------------------------

@Composable
private fun Greeting() {
    val (greetingRes, promptRes) = remember {
        when (LocalTime.now().hour) {
            in 5..11 -> R.string.greeting_morning to R.string.greeting_morning_prompt
            in 12..16 -> R.string.greeting_afternoon to R.string.greeting_afternoon_prompt
            in 17..21 -> R.string.greeting_evening to R.string.greeting_evening_prompt
            else -> R.string.greeting_night to R.string.greeting_night_prompt
        }
    }
    val greeting = stringResource(greetingRes)
    val prompt = stringResource(promptRes)
    Column(
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.m,
            end = MaterialTheme.spacing.m,
            top = MaterialTheme.spacing.s
        )
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = prompt,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Same look as SectionHeader (icon + bold primary title, 16dp top / 8dp bottom), plus an
// optional trailing action like "See all".
@Composable
private fun HomeSectionHeader(
    icon: ImageVector,
    title: String,
    action: (@Composable () -> Unit)? = null
) {
    val fadeThrough = AppAnimations.Transitions.rememberExpressiveFadeThrough()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = MaterialTheme.spacing.m,
                end = if (action != null) MaterialTheme.spacing.xs else MaterialTheme.spacing.m,
                top = MaterialTheme.spacing.l,
                bottom = MaterialTheme.spacing.sm
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(MaterialTheme.spacing.sm))
        // Fades through when a loading title ("Taste of the world") becomes the real one.
        AnimatedContent(
            targetState = title,
            transitionSpec = { fadeThrough() },
            label = "HomeSectionTitle",
            modifier = Modifier.weight(1f)
        ) { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        action?.invoke()
    }
}

@Composable
private fun SeeAllButton(enabled: Boolean = true, onClick: () -> Unit) {
    // Shown (disabled) while loading so the header keeps the same height.
    TextButton(onClick = onClick, enabled = enabled) { Text(stringResource(R.string.see_all)) }
}

// --- Featured carousel ------------------------------------------------------------------------

@Composable
private fun FeaturedCarousel(
    recipes: List<Recipe>,
    onClick: (Recipe) -> Unit,
    onCurrentImageChange: (String?) -> Unit
) {
    val context = LocalContext.current
    // The set on screen. A shuffle's new set only replaces it once its first photos are
    // downloaded, then crossfades in, so the old cards fade out instead of snapping to empty
    // cards that fill in one by one.
    var shown by remember { mutableStateOf(recipes) }
    LaunchedEffect(recipes) {
        if (recipes == shown) return@LaunchedEffect
        // Only the first two pages are visible at once. Capped so a slow connection still
        // swaps (the photos then fade in as they arrive).
        withTimeoutOrNull(FEATURED_PRELOAD_TIMEOUT_MILLIS) {
            recipes.take(2).map { recipe ->
                async { context.imageLoader.execute(ImageRequest.Builder(context).data(recipe.image).build()) }
            }.awaitAll()
        }
        shown = recipes
    }

    Crossfade(
        targetState = shown,
        animationSpec = tween(HOME_REVEAL_MILLIS),
        label = "featured_shuffle"
    ) { set ->
        FeaturedPager(recipes = set, onClick = onClick, onCurrentImageChange = onCurrentImageChange)
    }
}

// How long a shuffle waits for the new photos before swapping anyway.
private const val FEATURED_PRELOAD_TIMEOUT_MILLIS = 3_000L

@Composable
private fun FeaturedPager(
    recipes: List<Recipe>,
    onClick: (Recipe) -> Unit,
    onCurrentImageChange: (String?) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { recipes.size })

    // Drives the blurred parallax photo behind the top of the screen.
    LaunchedEffect(pagerState.currentPage, recipes) {
        onCurrentImageChange(recipes.getOrNull(pagerState.currentPage)?.image)
    }

    Column {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.m),
            pageSpacing = MaterialTheme.spacing.s,
            key = { recipes[it].id }
        ) { page ->
            FeaturedCard(recipe = recipes[page], onClick = { onClick(recipes[page]) })
        }

        PageDots(count = recipes.size, selected = pagerState.currentPage)
    }
}

// Quill's Discover page indicator, exactly: 8dp dots 6dp apart, start-aligned 16dp in and 16dp
// under the cards. The current page's dot stretches to 24dp on a bouncy spring and fades to the
// primary colour; the others are outlineVariant.
@Composable
private fun PageDots(count: Int, selected: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) { index ->
            val isSelected = selected == index

            val animatedWidth by animateDpAsState(
                targetValue = if (isSelected) 24.dp else 8.dp,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "dot_width"
            )
            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                label = "dot_color"
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .height(8.dp)
                    .width(animatedWidth)
                    .clip(CircleShape)
                    .background(animatedColor)
            )
        }
    }
}

@Composable
private fun FeaturedCard(recipe: Recipe, onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current

    Card(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
            onClick()
        },
        shape = RoundedCornerShape(MaterialTheme.corners.extraLarge),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = recipe.image,
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.45f to Color.Transparent,
                            1f to Color.Black.copy(alpha = 0.8f)
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(MaterialTheme.spacing.l)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)) {
                    recipe.area?.let { OverlayPill("${CuisineFlags.flag(it)}  $it") }
                    recipe.category?.let { OverlayPill(it) }
                }
                Spacer(Modifier.height(MaterialTheme.spacing.s))
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = pluralStringResource(R.plurals.home_ingredients_count, recipe.ingredients.size, recipe.ingredients.size) +
                        "  •  " + pluralStringResource(R.plurals.home_steps_count, recipe.steps.size, recipe.steps.size),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun OverlayPill(text: String) {
    Surface(
        shape = RoundedCornerShape(MaterialTheme.corners.pill),
        color = Color.White.copy(alpha = 0.22f),
        contentColor = Color.White
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// --- Quick picks ------------------------------------------------------------------------------

// Three grouped shortcut cards, Quill's glance-card treatment.
@Composable
private fun QuickPicks(
    onSurprise: () -> Unit,
    onBrowse: (RecipeFilter, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        QuickPickCard(
            title = stringResource(R.string.home_surprise_me),
            icon = Icons.Rounded.Shuffle,
            container = MaterialTheme.colorScheme.primaryContainer,
            content = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = rowItemShape(0, 3),
            onClick = onSurprise,
            modifier = Modifier.weight(1f)
        )
        QuickPickCard(
            title = stringResource(R.string.home_vegetarian),
            icon = Icons.Rounded.Eco,
            container = MaterialTheme.colorScheme.secondaryContainer,
            content = MaterialTheme.colorScheme.onSecondaryContainer,
            shape = rowItemShape(1, 3),
            onClick = { onBrowse(RecipeFilter.CATEGORY, "Vegetarian") },
            modifier = Modifier.weight(1f)
        )
        QuickPickCard(
            title = stringResource(R.string.home_desserts),
            icon = Icons.Rounded.Cake,
            container = MaterialTheme.colorScheme.tertiaryContainer,
            content = MaterialTheme.colorScheme.onTertiaryContainer,
            shape = rowItemShape(2, 3),
            onClick = { onBrowse(RecipeFilter.CATEGORY, "Dessert") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickPickCard(
    title: String,
    icon: ImageVector,
    container: Color,
    content: Color,
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
        colors = CardDefaults.cardColors(containerColor = container, contentColor = content),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MaterialTheme.spacing.m, horizontal = MaterialTheme.spacing.s)
        ) {
            Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null)
            }
            Spacer(Modifier.height(MaterialTheme.spacing.s))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

// --- Rows & tiles -----------------------------------------------------------------------------

@Composable
private fun CategoryTile(category: Category, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val haptic = LocalHapticFeedback.current

    Card(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
            onClick()
        },
        shape = RoundedCornerShape(MaterialTheme.corners.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier
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

// --- Loading & error --------------------------------------------------------------------------

// Same size as FeaturedCarousel: a card the width of one pager page (screen minus the pager's
// side padding) at the card's 0.9 aspect ratio, then the page dots underneath. Only the card
// pulses; the dots are drawn as the real indicator.
@Composable
private fun FeaturedCarouselPlaceholder() {
    Column {
        Box(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.m)
                .fillMaxWidth()
                .aspectRatio(0.9f)
                .clip(RoundedCornerShape(MaterialTheme.corners.extraLarge))
                .loadingPulse()
        )
        // The real page indicator, not a skeleton: FeaturedPager's own dots on its first page, so
        // it already looks loaded and doesn't change when the cards arrive
        PageDots(count = FEATURED_PLACEHOLDER_DOTS, selected = 0)
    }
}

// The featured carousel shows 5 recipes (HomeViewModel's getRandomMeals(count = 5)).
private const val FEATURED_PLACEHOLDER_DOTS = 5

// Same layout as CategoryTile: the card, its padding, the 1.3 image and one line of label.
@Composable
private fun CategoryTilePlaceholder(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(MaterialTheme.corners.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier
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
