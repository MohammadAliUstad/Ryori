package com.yugentech.ryori.ui.main.homeScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yugentech.ryori.ui.main.mainScreen.components.ParallaxBackground
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yugentech.ryori.api.model.domain.Category
import com.yugentech.ryori.api.model.domain.CuisineFlags
import com.yugentech.ryori.api.model.domain.Recipe
import com.yugentech.ryori.api.model.domain.RecipeFilter
import com.yugentech.ryori.api.model.domain.RecipeSummary
import com.yugentech.ryori.api.model.domain.RecipeType
import com.yugentech.ryori.api.viewmodel.HomeUiState
import com.yugentech.ryori.api.viewmodel.HomeViewModel
import com.yugentech.ryori.theme.service.HapticService
import com.yugentech.ryori.theme.tokens.corners
import com.yugentech.ryori.theme.tokens.spacing
import com.yugentech.ryori.ui.main.mainScreen.components.RecipeCardRow
import com.yugentech.ryori.ui.main.mainScreen.components.rowItemShape
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalTime

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

    // Same as Quill's Library: the bar is transparent over a blurred parallax photo, and a
    // surface gradient fades in behind it as the content scrolls up.
    val scrollAlpha by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex > 0) 1f
            else (listState.firstVisibleItemScrollOffset / 100f).coerceIn(0f, 1f)
        }
    }
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
            val surfaceColor = MaterialTheme.colorScheme.surface
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
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
                TopAppBar(
                    title = {
                        Text(
                            text = "Ryori",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    scrollBehavior = scrollBehavior
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        val listPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding() + MaterialTheme.spacing.s
        )

        Box(modifier = Modifier.fillMaxSize()) {
            // Drawn from the very top (under the transparent bar) so the photo shows behind it.
            if (!uiState.isLoading && parallaxImage != null) {
                ParallaxBackground(
                    imageUrl = parallaxImage,
                    scrollOffset = parallaxScrollOffset,
                    headerHeight = 440.dp
                )
            }

            AnimatedContent(
                targetState = when {
                    uiState.isLoading -> HomeMode.Loading
                    uiState.featured.isEmpty() -> HomeMode.Error
                    else -> HomeMode.Content
                },
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "HomeMode"
            ) { mode ->
                when (mode) {
                    HomeMode.Loading -> HomeSkeleton(contentPadding = listPadding)
                    HomeMode.Error -> ErrorContent(
                        message = uiState.error ?: "Something went wrong",
                        onRetry = viewModel::load
                    )

                    HomeMode.Content -> HomeContent(
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
        }
    }
}

private enum class HomeMode { Loading, Error, Content }

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
                    title = "Featured today",
                    action = {
                        IconButton(onClick = onShuffleFeatured, enabled = !uiState.isRefreshingFeatured) {
                            if (uiState.isRefreshingFeatured) {
                                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                            } else {
                                Icon(Icons.Rounded.Refresh, contentDescription = "New picks")
                            }
                        }
                    }
                )
                FeaturedCarousel(
                    recipes = uiState.featured,
                    onClick = { onRecipeClick(it.type, it.id) },
                    onCurrentImageChange = onFeaturedImageChange
                )
            }
        }

        item(key = "quick_picks") {
            QuickPicks(
                surpriseLoading = uiState.isSurpriseLoading,
                onSurprise = onSurprise,
                onBrowse = onBrowse,
                modifier = Modifier.padding(
                    start = MaterialTheme.spacing.m,
                    end = MaterialTheme.spacing.m,
                    top = MaterialTheme.spacing.l
                )
            )
        }

        uiState.cuisineSpotlight?.let { area ->
            item(key = "cuisine_header") {
                HomeSectionHeader(
                    icon = Icons.Rounded.Public,
                    title = "Taste of ${CuisineFlags.flag(area)} $area",
                    action = { SeeAllButton { onBrowse(RecipeFilter.AREA, area) } }
                )
            }
            item(key = "cuisine_row") {
                RecipeCardRow(recipes = uiState.cuisineRecipes, onClick = openSummary, contentPadding = rowPadding)
            }
        }

        uiState.categorySpotlight?.let { category ->
            item(key = "category_header") {
                HomeSectionHeader(
                    icon = Icons.Rounded.RestaurantMenu,
                    title = "$category favourites",
                    action = { SeeAllButton { onBrowse(RecipeFilter.CATEGORY, category) } }
                )
            }
            item(key = "category_row") {
                RecipeCardRow(recipes = uiState.categoryRecipes, onClick = openSummary, contentPadding = rowPadding)
            }
        }

        if (uiState.drinks.isNotEmpty()) {
            item(key = "drinks_header") {
                HomeSectionHeader(
                    icon = Icons.Rounded.LocalCafe,
                    title = "Something to sip",
                    action = { SeeAllButton { onBrowse(RecipeFilter.DRINKS, "all") } }
                )
            }
            item(key = "drinks_row") {
                RecipeCardRow(recipes = uiState.drinks, onClick = openSummary, contentPadding = rowPadding)
            }
        }

        if (uiState.categories.isNotEmpty()) {
            item(key = "categories_header") {
                HomeSectionHeader(icon = Icons.Rounded.Category, title = "Browse by category")
            }
            val rows = uiState.categories.chunked(3)
            rows.forEachIndexed { rowIndex, row ->
                item(key = "category_grid_$rowIndex") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.m)
                            .padding(bottom = if (rowIndex < rows.lastIndex) MaterialTheme.spacing.s else 0.dp)
                    ) {
                        row.forEach { category ->
                            CategoryTile(
                                category = category,
                                onClick = { category.name?.let { onBrowse(RecipeFilter.CATEGORY, it) } },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Keep tiles the same width on a short last row.
                        repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

// --- Greeting & headers -----------------------------------------------------------------------

@Composable
private fun Greeting() {
    val (greeting, prompt) = remember {
        when (LocalTime.now().hour) {
            in 5..11 -> "Good morning ☀️" to "What's for breakfast?"
            in 12..16 -> "Good afternoon 🌤️" to "Let's plan something tasty"
            in 17..21 -> "Good evening 🌙" to "What are we cooking tonight?"
            else -> "Late night cravings? 🌙" to "Something quick and comforting"
        }
    }
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
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        action?.invoke()
    }
}

@Composable
private fun SeeAllButton(onClick: () -> Unit) {
    TextButton(onClick = onClick) { Text("See all") }
}

// --- Featured carousel ------------------------------------------------------------------------

@Composable
private fun FeaturedCarousel(
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

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.spacing.s)
        ) {
            repeat(recipes.size) { index ->
                val selected = pagerState.currentPage == index
                val width by animateDpAsState(if (selected) 20.dp else 6.dp, label = "dot")
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }
    }
}

@Composable
private fun FeaturedCard(recipe: Recipe, onClick: () -> Unit) {
    val haptic = koinInject<HapticService>()
    val view = LocalView.current

    Card(
        onClick = {
            haptic.performHaptic(view)
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
                    text = "${recipe.ingredients.size} ingredients  •  ${recipe.steps.size} steps",
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
    surpriseLoading: Boolean,
    onSurprise: () -> Unit,
    onBrowse: (RecipeFilter, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        QuickPickCard(
            title = "Surprise me",
            icon = Icons.Rounded.Shuffle,
            loading = surpriseLoading,
            container = MaterialTheme.colorScheme.primaryContainer,
            content = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = rowItemShape(0, 3),
            onClick = onSurprise,
            modifier = Modifier.weight(1f)
        )
        QuickPickCard(
            title = "Vegetarian",
            icon = Icons.Rounded.Eco,
            container = MaterialTheme.colorScheme.secondaryContainer,
            content = MaterialTheme.colorScheme.onSecondaryContainer,
            shape = rowItemShape(1, 3),
            onClick = { onBrowse(RecipeFilter.CATEGORY, "Vegetarian") },
            modifier = Modifier.weight(1f)
        )
        QuickPickCard(
            title = "Desserts",
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
    modifier: Modifier = Modifier,
    loading: Boolean = false
) {
    val haptic = koinInject<HapticService>()
    val view = LocalView.current

    Card(
        onClick = {
            haptic.performHaptic(view)
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
                if (loading) {
                    CircularProgressIndicator(color = content, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                } else {
                    Icon(imageVector = icon, contentDescription = null)
                }
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
    val haptic = koinInject<HapticService>()
    val view = LocalView.current

    Card(
        onClick = {
            haptic.performHaptic(view)
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

@Composable
private fun HomeSkeleton(contentPadding: PaddingValues) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "shimmerAlpha"
    )
    val block: @Composable (Modifier) -> Unit = { modifier ->
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(MaterialTheme.corners.large))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = alpha))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = contentPadding.calculateTopPadding())
            .padding(horizontal = MaterialTheme.spacing.m)
    ) {
        Spacer(Modifier.height(MaterialTheme.spacing.s))
        block(Modifier.width(140.dp).height(18.dp))
        Spacer(Modifier.height(MaterialTheme.spacing.s))
        block(Modifier.fillMaxWidth(0.8f).height(30.dp))
        Spacer(Modifier.height(MaterialTheme.spacing.l))
        block(Modifier.fillMaxWidth().aspectRatio(0.9f))
        Spacer(Modifier.height(MaterialTheme.spacing.l))
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            repeat(3) { block(Modifier.weight(1f).height(84.dp)) }
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.xl)
    ) {
        Text(
            text = "Couldn't load recipes",
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
