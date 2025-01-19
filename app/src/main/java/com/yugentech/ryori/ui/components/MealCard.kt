package com.yugentech.ryori.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yugentech.ryori.data.model.domain.DomainMeal

@Composable
fun MealCard(
    meal: DomainMeal?,
    onMealClick: (DomainMeal) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onMealClick(
                    meal ?: DomainMeal(
                        area = null,
                        category = null,
                        instructions = null,
                        name = null,
                        image = null,
                        tags = null,
                        ingredients = listOf(),
                        measures = listOf()
                    )
                )
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Meal Image
            AsyncImage(
                model = meal?.image,
                contentDescription = meal?.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(shape = RoundedCornerShape(16.dp)), // Rounded corners for images
                contentScale = ContentScale.Crop
            )

            // Meal Name with background for contrast
            Text(
                text = meal?.name ?: "Unknown Meal",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp), // Padding around text
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}