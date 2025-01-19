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
import com.yugentech.ryori.data.model.domain.DomainCategory

@Composable
fun CategoryCard(
    category: DomainCategory,
    onCategoryClick: (String) -> Unit
) {
    if (category.name != "Breakfast" && category.name != "Goat") {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    onCategoryClick(category.name ?: "")
                },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween // Ensure even spacing between image and text
            ) {
                // Category Image
                AsyncImage(
                    model = category.image,
                    contentDescription = category.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(shape = RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = category.name ?: "Unknown Category",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}