package com.yugentech.ryori.ui.config.settingsScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yugentech.ryori.data.stats.KitchenStats
import com.yugentech.ryori.theme.service.HapticService
import com.yugentech.ryori.theme.tokens.corners
import com.yugentech.ryori.theme.tokens.icons
import com.yugentech.ryori.theme.tokens.spacing
import com.yugentech.ryori.ui.main.mainScreen.components.rowItemShape
import org.koin.compose.koinInject
import java.util.Locale

// Top of the More tab: who's cooking, their "rank", and a few stats from how they use the app.
@Composable
fun ChefCard(
    chefName: String,
    stats: KitchenStats,
    onEditName: () -> Unit
) {
    val haptic = koinInject<HapticService>()
    val view = LocalView.current

    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(
                topStart = MaterialTheme.corners.extraLarge,
                topEnd = MaterialTheme.corners.extraLarge,
                bottomStart = MaterialTheme.corners.small,
                bottomEnd = MaterialTheme.corners.small
            )
        ) {
            Row(
                modifier = Modifier.padding(MaterialTheme.spacing.l),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hello,",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = chefName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = chefRank(stats.recipesViewed),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = MaterialTheme.spacing.xxs)
                    )
                }

                IconButton(
                    onClick = {
                        haptic.performHaptic(view)
                        onEditName()
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit name",
                        modifier = Modifier.size(MaterialTheme.icons.mediumSmall)
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)) {
            StatTile(
                value = compactCount(stats.recipesViewed),
                label = "Recipes viewed",
                index = 0,
                modifier = Modifier.weight(1f)
            )
            StatTile(
                value = compactCount(stats.ingredientsTicked),
                label = "Ingredients ticked",
                index = 1,
                modifier = Modifier.weight(1f)
            )
            StatTile(
                value = stats.favouriteCuisine ?: "None yet",
                label = "Favourite cuisine",
                index = 2,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatTile(
    value: String,
    label: String,
    index: Int,
    modifier: Modifier = Modifier
) {
    // Bottom corners match the card above: large on the outside of the row, small inside.
    val shape = when (index) {
        0 -> RoundedCornerShape(
            topStart = MaterialTheme.corners.small,
            topEnd = MaterialTheme.corners.small,
            bottomStart = MaterialTheme.corners.extraLarge,
            bottomEnd = MaterialTheme.corners.small
        )

        2 -> RoundedCornerShape(
            topStart = MaterialTheme.corners.small,
            topEnd = MaterialTheme.corners.small,
            bottomStart = MaterialTheme.corners.small,
            bottomEnd = MaterialTheme.corners.extraLarge
        )

        else -> rowItemShape(index = 1, count = 3)
    }

    Column(
        modifier = modifier
            .height(92.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.sm),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            // No minLines: a one-line label would otherwise leave an empty line under it and
            // push the tile's content off centre. The tile's fixed height keeps the row even.
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}

// A playful title that grows with the number of recipes opened.
private fun chefRank(recipesViewed: Long): String = when {
    recipesViewed >= 100 -> "Master chef"
    recipesViewed >= 50 -> "Head chef"
    recipesViewed >= 20 -> "Sous chef"
    recipesViewed >= 5 -> "Home cook"
    else -> "Kitchen newbie"
}

private fun compactCount(count: Long): String = when {
    count >= 1_000_000 -> String.format(Locale.US, "%.1fM", count / 1_000_000.0)
    count >= 10_000 -> "${count / 1_000}k"
    count >= 1_000 -> String.format(Locale.US, "%.1fk", count / 1_000.0)
    else -> count.toString()
}
