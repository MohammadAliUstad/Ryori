package com.yugentech.ryori.ui.main.mainScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yugentech.ryori.api.model.domain.RecipeSummary
import com.yugentech.ryori.api.model.domain.RecipeType
import com.yugentech.ryori.theme.service.HapticService
import com.yugentech.ryori.theme.tokens.corners
import com.yugentech.ryori.theme.tokens.spacing
import org.koin.compose.koinInject

// Width of cards in horizontal rows.
val RecipeRowCardWidth: Dp = 150.dp

@Composable
private fun recipeTitleStyle(): TextStyle =
    MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)

// Square photo + name card used by every recipe row and grid. Drinks get a small cup badge so
// mixed search results are easy to tell apart.
//
// titleLines reserves room for the name: 2 keeps every card the same height regardless of
// name length (the default, right for grids); RecipeCardRow passes 1 when no name in the row
// wraps, so a row of short names isn't padded out.
@Composable
fun RecipeCard(
    recipe: RecipeSummary,
    onClick: (RecipeSummary) -> Unit,
    modifier: Modifier = Modifier,
    titleLines: Int = 2
) {
    val haptic = koinInject<HapticService>()
    val view = LocalView.current

    // Only the photo is clipped to the rounded shape. Clipping the whole card would also cut
    // the corners off the name text underneath it.
    Column(
        modifier = modifier.clickable {
            haptic.performHaptic(view)
            onClick(recipe)
        }
    ) {
        Box {
            AsyncImage(
                model = recipe.image,
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(MaterialTheme.corners.large))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            )

            if (recipe.type == RecipeType.DRINK) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(MaterialTheme.spacing.s)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocalCafe,
                        contentDescription = "Drink",
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }

        Text(
            text = recipe.name,
            style = recipeTitleStyle(),
            color = MaterialTheme.colorScheme.onSurface,
            minLines = titleLines.coerceIn(1, 2),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(
                start = MaterialTheme.spacing.xs,
                end = MaterialTheme.spacing.xs,
                top = MaterialTheme.spacing.s,
                bottom = MaterialTheme.spacing.xs
            )
        )
    }
}

// Horizontal row of RecipeCards whose height never changes while scrolling (same approach as
// Quill's library BookRow): every name is measured up front, and if any of them wraps, every
// card reserves two lines. Otherwise a two-line name scrolling into view would make the row
// taller and push everything below it down.
@Composable
fun RecipeCardRow(
    recipes: List<RecipeSummary>,
    onClick: (RecipeSummary) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    // null measures the names (see above). Pass 2 to always reserve two lines, e.g. when a
    // placeholder row of the same height is shown while loading.
    titleLines: Int? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = recipeTitleStyle()
    val density = LocalDensity.current
    // Card width minus the name's horizontal padding.
    val textWidthPx = with(density) { (RecipeRowCardWidth - MaterialTheme.spacing.xs * 2).roundToPx() }

    val measuredTitleLines = remember(recipes, textStyle, textWidthPx) {
        val anyWraps = recipes.any { recipe ->
            textMeasurer.measure(
                text = recipe.name,
                style = textStyle,
                constraints = Constraints(maxWidth = textWidthPx),
                maxLines = 2
            ).lineCount > 1
        }
        if (anyWraps) 2 else 1
    }
    val cardTitleLines = titleLines ?: measuredTitleLines

    LazyRow(
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
        modifier = modifier
    ) {
        items(recipes, key = { "${it.type}_${it.id}" }) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = onClick,
                titleLines = cardTitleLines,
                modifier = Modifier.width(RecipeRowCardWidth)
            )
        }
        if (trailing != null) {
            item(key = "row_trailing") { trailing() }
        }
    }
}

// Loading placeholder for RecipeCardRow: same card width, square photo, spacing and title
// height (titleLines), so swapping in the real row doesn't move anything on screen.
@Composable
fun RecipeCardRowPlaceholder(
    contentPadding: PaddingValues,
    shimmer: Color,
    modifier: Modifier = Modifier,
    titleLines: Int = 2,
    count: Int = 4
) {
    LazyRow(
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
        userScrollEnabled = false,
        modifier = modifier
    ) {
        items(count) {
            Column(modifier = Modifier.width(RecipeRowCardWidth)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(MaterialTheme.corners.large))
                        .background(shimmer)
                )
                // An empty Text with the card title's style and line count takes exactly the
                // height the name will.
                Text(
                    text = "",
                    style = recipeTitleStyle(),
                    minLines = titleLines,
                    modifier = Modifier
                        .padding(
                            start = MaterialTheme.spacing.xs,
                            end = MaterialTheme.spacing.xs,
                            top = MaterialTheme.spacing.s,
                            bottom = MaterialTheme.spacing.xs
                        )
                        .fillMaxWidth(0.8f)
                        .clip(RoundedCornerShape(MaterialTheme.corners.small))
                        .background(shimmer)
                )
            }
        }
    }
}
