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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
// titleLines is both the reserved and the maximum line count for the name, so every card is the
// same height: 2 for grids (the default), 1 for RecipeCardRow.
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
            minLines = titleLines,
            maxLines = titleLines,
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

// Horizontal row of RecipeCards. Names are kept to one line (ellipsized) so every card is the
// same height and the row never grows while scrolling.
@Composable
fun RecipeCardRow(
    recipes: List<RecipeSummary>,
    onClick: (RecipeSummary) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null
) {
    LazyRow(
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
        modifier = modifier
    ) {
        items(recipes, key = { "${it.type}_${it.id}" }) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = onClick,
                titleLines = 1,
                modifier = Modifier.width(RecipeRowCardWidth)
            )
        }
        if (trailing != null) {
            item(key = "row_trailing") { trailing() }
        }
    }
}

// Loading placeholder for RecipeCardRow: same card width, square photo, spacing and title
// height (one line), so swapping in the real row doesn't move anything on screen.
@Composable
fun RecipeCardRowPlaceholder(
    contentPadding: PaddingValues,
    shimmer: Color,
    modifier: Modifier = Modifier,
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
                // An empty Text with the card title's style takes exactly the height the name will.
                Text(
                    text = "",
                    style = recipeTitleStyle(),
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
