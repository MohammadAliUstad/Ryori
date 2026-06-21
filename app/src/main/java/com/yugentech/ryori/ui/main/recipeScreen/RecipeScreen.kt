package com.yugentech.ryori.ui.main.recipeScreen

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.ShoppingBasket
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yugentech.ryori.api.model.domain.CuisineFlags
import com.yugentech.ryori.api.model.domain.DrinkCategories
import com.yugentech.ryori.api.model.domain.Recipe
import com.yugentech.ryori.api.model.domain.RecipeFilter
import com.yugentech.ryori.api.model.domain.RecipeIngredient
import com.yugentech.ryori.api.model.domain.RecipeSummary
import com.yugentech.ryori.api.model.domain.RecipeType
import com.yugentech.ryori.api.viewmodel.RecipeUiState
import com.yugentech.ryori.api.viewmodel.RecipeViewModel
import com.yugentech.ryori.theme.service.HapticService
import com.yugentech.ryori.theme.tokens.corners
import com.yugentech.ryori.theme.tokens.spacing
import com.yugentech.ryori.ui.main.mainScreen.components.RecipeCardRow
import com.yugentech.ryori.ui.main.mainScreen.components.SectionHeader
import com.yugentech.ryori.ui.main.mainScreen.components.itemShape
import com.yugentech.ryori.ui.main.mainScreen.components.rowItemShape
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(
    type: RecipeType,
    id: String,
    onBack: () -> Unit,
    onRecipeClick: (RecipeType, String) -> Unit,
    onBrowse: (RecipeFilter, String) -> Unit,
    viewModel: RecipeViewModel = koinViewModel()
) {
    LaunchedEffect(type, id) { viewModel.load(type, id) }

    val uiState by viewModel.uiState.collectAsState()
    val keepScreenOn by viewModel.keepScreenOn.collectAsState()

    // "Keep screen on" (More > Your Kitchen): no dimming while cooking from this recipe.
    val rootView = LocalView.current
    DisposableEffect(rootView, keepScreenOn) {
        rootView.keepScreenOn = keepScreenOn
        onDispose { rootView.keepScreenOn = false }
    }

    val listState = rememberLazyListState()
    val context = LocalContext.current
    val density = LocalDensity.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    // Same trigger as Quill's book details: show the title once the page has scrolled a bit.
    val showTopBarTitle by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 ||
                    listState.firstVisibleItemScrollOffset > with(density) { 50.dp.toPx() }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            val recipe = uiState.recipe
            RecipeDetailsTopBar(
                title = recipe?.name.orEmpty(),
                subtitle = recipe?.let { listOfNotNull(it.category, it.area ?: it.glass).joinToString("  •  ") },
                isVisible = showTopBarTitle,
                onBackClick = onBack,
                onShareClick = recipe?.let { { context.startActivity(shareIntent(it)) } },
                scrollBehavior = scrollBehavior
            )
        }
    ) { _ ->
        val recipe = uiState.recipe
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            recipe == null -> ErrorState(
                message = uiState.error ?: "Couldn't load this recipe",
                onRetry = { viewModel.load(type, id, force = true) }
            )

            else -> RecipeContent(
                recipe = recipe,
                uiState = uiState,
                listState = listState,
                onToggleIngredient = viewModel::toggleIngredient,
                onRecipeClick = { onRecipeClick(it.type, it.id) },
                onBrowse = onBrowse
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecipeContent(
    recipe: Recipe,
    uiState: RecipeUiState,
    listState: LazyListState,
    onToggleIngredient: (Int) -> Unit,
    onRecipeClick: (RecipeSummary) -> Unit,
    onBrowse: (RecipeFilter, String) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val horizontal = Modifier.padding(horizontal = MaterialTheme.spacing.m)

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = MaterialTheme.spacing.s)
    ) {
        item(key = "hero") { Hero(recipe) }

        item(key = "title") {
            Column(modifier = horizontal) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(MaterialTheme.spacing.s))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
                ) {
                    recipe.category?.let { category ->
                        val (filter, label) = when (recipe.type) {
                            RecipeType.MEAL -> RecipeFilter.CATEGORY to category
                            RecipeType.DRINK -> RecipeFilter.DRINK_CATEGORY to DrinkCategories.label(category)
                        }
                        InfoChip(
                            label = label,
                            container = MaterialTheme.colorScheme.primaryContainer,
                            content = MaterialTheme.colorScheme.onPrimaryContainer,
                            onClick = { onBrowse(filter, category) }
                        )
                    }
                    recipe.area?.let { area ->
                        InfoChip(
                            label = "${CuisineFlags.flag(area)}  $area",
                            container = MaterialTheme.colorScheme.secondaryContainer,
                            content = MaterialTheme.colorScheme.onSecondaryContainer,
                            onClick = { onBrowse(RecipeFilter.AREA, area) }
                        )
                    }
                    recipe.drinkStyle?.let { style ->
                        InfoChip(
                            label = style,
                            container = MaterialTheme.colorScheme.tertiaryContainer,
                            content = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    recipe.glass?.let { glass ->
                        InfoChip(
                            label = glass,
                            container = MaterialTheme.colorScheme.surfaceContainerHigh,
                            content = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    recipe.tags.forEach { tag ->
                        InfoChip(
                            label = "#$tag",
                            container = MaterialTheme.colorScheme.surfaceContainerHigh,
                            content = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (recipe.videoUrl != null || recipe.sourceUrl != null) {
            item(key = "actions") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                    modifier = horizontal
                        .fillMaxWidth()
                        .padding(top = MaterialTheme.spacing.m)
                ) {
                    recipe.videoUrl?.let { url ->
                        Button(onClick = { uriHandler.openUri(url) }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Rounded.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(MaterialTheme.spacing.s))
                            Text("Watch video")
                        }
                    }
                    recipe.sourceUrl?.let { url ->
                        FilledTonalButton(onClick = { uriHandler.openUri(url) }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.AutoMirrored.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(MaterialTheme.spacing.s))
                            Text("Source")
                        }
                    }
                }
            }
        }

        item(key = "stats") {
            GlanceRow(recipe = recipe, modifier = horizontal.padding(top = MaterialTheme.spacing.m))
        }

        if (recipe.ingredients.isNotEmpty()) {
            item(key = "ingredients_header") {
                Column {
                    SectionHeader(icon = Icons.Rounded.ShoppingBasket, title = "Ingredients")
                    Text(
                        text = "${uiState.checkedIngredients.size} of ${recipe.ingredients.size} ready. Tap to tick off.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = horizontal.padding(bottom = MaterialTheme.spacing.s)
                    )
                }
            }
            itemsIndexed(recipe.ingredients, key = { index, it -> "ingredient_${index}_${it.name}" }) { index, ingredient ->
                IngredientRow(
                    ingredient = ingredient,
                    checked = index in uiState.checkedIngredients,
                    shape = itemShape(index, recipe.ingredients.size),
                    onToggle = { onToggleIngredient(index) },
                    modifier = horizontal.padding(bottom = 2.dp)
                )
            }
        }

        if (recipe.steps.isNotEmpty()) {
            item(key = "method_header") {
                SectionHeader(icon = Icons.Rounded.FormatListNumbered, title = "Method")
            }
            itemsIndexed(recipe.steps, key = { index, _ -> "step_$index" }) { index, step ->
                StepRow(
                    number = index + 1,
                    text = step,
                    shape = itemShape(index, recipe.steps.size),
                    modifier = horizontal.padding(bottom = 2.dp)
                )
            }
        }

        if (uiState.related.isNotEmpty()) {
            item(key = "related_header") {
                SectionHeader(
                    icon = Icons.Rounded.AutoAwesome,
                    title = if (recipe.type == RecipeType.MEAL) "More ${recipe.category.orEmpty()} dishes" else "More drinks"
                )
            }
            item(key = "related") {
                RecipeCardRow(
                    recipes = uiState.related,
                    onClick = onRecipeClick,
                    contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.m)
                )
            }
        }

        item(key = "nav_bar_space") {
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

@Composable
private fun Hero(recipe: Recipe) {
    val surface = MaterialTheme.colorScheme.surface
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        AsyncImage(
            model = recipe.image,
            contentDescription = recipe.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        )
        // Fades the photo into the page so the title reads as part of the same surface.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.55f to Color.Transparent,
                        1f to surface
                    )
                )
        )
    }
}

@Composable
private fun InfoChip(
    label: String,
    container: Color,
    content: Color,
    onClick: (() -> Unit)? = null
) {
    val haptic = koinInject<HapticService>()
    val view = LocalView.current
    val shape = RoundedCornerShape(MaterialTheme.corners.pill)

    // Every chip gets the same fixed height with its text centred. Tappable ones use a
    // clickable modifier rather than Surface(onClick), which would enforce Material's 48dp
    // minimum touch height and make them sit taller than the plain chips in the same row;
    // emoji (cuisine flags) also have a taller line box than plain text.
    Surface(
        shape = shape,
        color = container,
        contentColor = content,
        modifier = Modifier
            .height(32.dp)
            .clip(shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable {
                        haptic.performHaptic(view)
                        onClick()
                    }
                } else Modifier
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1
            )
        }
    }
}

// Grouped three-card summary, Quill's insights glance style.
@Composable
private fun GlanceRow(recipe: Recipe, modifier: Modifier = Modifier) {
    val third = when (recipe.type) {
        RecipeType.MEAL -> (recipe.area ?: recipe.category ?: "Dish") to "Cuisine"
        RecipeType.DRINK -> (recipe.glass ?: "Drink") to "Served in"
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        GlanceCard(
            value = recipe.ingredients.size.toString(),
            label = "Ingredients",
            container = MaterialTheme.colorScheme.primaryContainer,
            content = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = rowItemShape(0, 3),
            modifier = Modifier.weight(1f)
        )
        GlanceCard(
            value = recipe.steps.size.toString(),
            label = "Steps",
            container = MaterialTheme.colorScheme.secondaryContainer,
            content = MaterialTheme.colorScheme.onSecondaryContainer,
            shape = rowItemShape(1, 3),
            modifier = Modifier.weight(1f)
        )
        GlanceCard(
            value = third.first,
            label = third.second,
            container = MaterialTheme.colorScheme.tertiaryContainer,
            content = MaterialTheme.colorScheme.onTertiaryContainer,
            shape = rowItemShape(2, 3),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun GlanceCard(
    value: String,
    label: String,
    container: Color,
    content: Color,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    Card(
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
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = content.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun IngredientRow(
    ingredient: RecipeIngredient,
    checked: Boolean,
    shape: Shape,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = koinInject<HapticService>()
    val view = LocalView.current

    Surface(
        onClick = {
            haptic.performHaptic(view)
            onToggle()
        },
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.m, vertical = MaterialTheme.spacing.s)
        ) {
            AsyncImage(
                model = ingredient.image,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    .padding(6.dp)
            )
            Spacer(Modifier.width(MaterialTheme.spacing.m))
            Text(
                text = ingredient.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                textDecoration = if (checked) TextDecoration.LineThrough else null,
                color = if (checked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            if (ingredient.measure.isNotEmpty()) {
                Text(
                    text = ingredient.measure,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.s)
                )
            }
            Icon(
                imageVector = if (checked) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                contentDescription = if (checked) "Ticked" else "Not ticked",
                tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun StepRow(number: Int, text: String, shape: Shape, modifier: Modifier = Modifier) {
    // One-line steps are centred on the number circle. Longer steps stay top-aligned, with the
    // text nudged down so its first line sits level with the circle instead of above it.
    var lineCount by remember(text) { mutableIntStateOf(1) }
    val isSingleLine = lineCount <= 1

    Surface(
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = if (isSingleLine) Alignment.CenterVertically else Alignment.Top,
            modifier = Modifier.padding(MaterialTheme.spacing.m)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = number.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.width(MaterialTheme.spacing.m))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                onTextLayout = { layout -> lineCount = layout.lineCount },
                modifier = Modifier
                    .weight(1f)
                    .padding(top = if (isSingleLine) 0.dp else 4.dp)
            )
        }
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
            text = "Couldn't load this recipe",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(MaterialTheme.spacing.xs))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(MaterialTheme.spacing.m))
        Button(onClick = onRetry) { Text("Try again") }
    }
}

private fun shareIntent(recipe: Recipe): Intent {
    val link = recipe.sourceUrl ?: recipe.videoUrl
    val text = buildString {
        append("Try this ")
        append(if (recipe.type == RecipeType.MEAL) "recipe" else "drink")
        append(" on Ryori: ")
        append(recipe.name)
        if (link != null) append("\n").append(link)
    }
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    return Intent.createChooser(send, "Share recipe")
}
