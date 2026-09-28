package com.yugentech.ryori.ui.config.attributionsScreen.components

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.net.toUri
import com.yugentech.ryori.theme.tokens.LocalDesignTokens

@Composable
fun MealDbCreditCard() {
    DataSourceCreditCard(
        icon = Icons.Default.Restaurant,
        title = "TheMealDB",
        subtitle = "Meals, cuisines and ingredients",
        body = "An open, crowd-sourced database of recipes from around the world. Every meal, category, cuisine and ingredient in Ryori comes from here.",
        buttonLabel = "Visit TheMealDB",
        url = "https://www.themealdb.com",
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        badgeColor = MaterialTheme.colorScheme.primary,
        badgeContentColor = MaterialTheme.colorScheme.onPrimary
    )
}

@Composable
fun CocktailDbCreditCard() {
    DataSourceCreditCard(
        icon = Icons.Default.LocalBar,
        title = "TheCocktailDB",
        subtitle = "Drinks and mocktails",
        body = "The sister database to TheMealDB, full of drinks. Ryori only shows its non-alcoholic ones, so every glass is one anyone can enjoy.",
        buttonLabel = "Visit TheCocktailDB",
        url = "https://www.thecocktaildb.com",
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        badgeColor = MaterialTheme.colorScheme.tertiary,
        badgeContentColor = MaterialTheme.colorScheme.onTertiary
    )
}

// Same layout as DesignCreditCard so the carousel pages line up.
@Composable
private fun DataSourceCreditCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    body: String,
    buttonLabel: String,
    url: String,
    containerColor: Color,
    contentColor: Color,
    badgeColor: Color,
    badgeContentColor: Color
) {
    val context = LocalContext.current
    val tokens = LocalDesignTokens.current

    Card(
        shape = RoundedCornerShape(tokens.corners.extraLarge),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = tokens.spacing.s)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(tokens.spacing.l),
            verticalArrangement = Arrangement.spacedBy(tokens.spacing.m)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = badgeColor,
                    modifier = Modifier.size(tokens.components.imageSizeSmall)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = badgeContentColor)
                    }
                }

                Spacer(Modifier.width(tokens.spacing.m))

                Column {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }
            }

            Text(
                body,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.8f),
                minLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            FilledTonalButton(
                onClick = { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) } },
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(tokens.corners.medium),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(buttonLabel)
            }
        }
    }
}
