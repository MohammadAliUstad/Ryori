package com.yugentech.ryori.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.yugentech.ryori.viewmodel.RyoriViewModel

@Composable
fun MealScreen(
    mealName: String?,
    viewModel: RyoriViewModel = hiltViewModel()
) {
    val meal by viewModel.meal.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(key1 = mealName) {
        if (!mealName.isNullOrBlank()) {
            viewModel.fetchMealByName(mealName)
        }
    }

    val scrollState = rememberScrollState()

    val imageSize by animateDpAsState(
        targetValue = if (scrollState.value > 0) 60.dp else 220.dp,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "Image Size Animation"
    )

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else if (error != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = error ?: "Unknown error",
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error),
                textAlign = TextAlign.Center
            )
        }
    } else {
        meal?.let {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "Meal Details",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 28.sp,
                            letterSpacing = 1.sp,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        textAlign = TextAlign.Center
                    )

                    Card(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AsyncImage(
                                model = it.image,
                                contentDescription = "Meal Image",
                                modifier = Modifier
                                    .size(imageSize)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.primary),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Text(
                        text = it.name ?: "Unknown",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            letterSpacing = 1.5.sp
                        ),
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Info:",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Category: ${it.category ?: "Unknown"}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Region: ${it.area ?: "Unknown"}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Light
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    var isIngredientsExpanded by remember { mutableStateOf(false) }

                    Text(
                        text = "Ingredients:",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .animateContentSize(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = 0.dp
                            )
                        ) {
                            it.ingredients.zip(it.measures)
                                .filter { (ingredient, _) -> ingredient.isNotBlank() }
                                .take(if (isIngredientsExpanded) Int.MAX_VALUE else 5)
                                .forEach { (ingredient, measure) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "$ingredient - $measure",
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 16.sp,
                                                lineHeight = 24.sp
                                            )
                                        )
                                    }
                                }
                        }
                        if (it.ingredients.size > 5) {
                            TextButton(
                                onClick = { isIngredientsExpanded = !isIngredientsExpanded },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(
                                    text = if (isIngredientsExpanded) "Show Less" else "Show More",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    var isInstructionsExpanded by remember { mutableStateOf(false) }

                    Text(
                        text = "Instructions:",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .animateContentSize(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = 0.dp
                            )
                        ) {
                            Text(
                                text = it.instructions ?: "No instructions provided",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    textAlign = TextAlign.Justify
                                ),
                                maxLines = if (isInstructionsExpanded) Int.MAX_VALUE else 4,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if ((it.instructions?.length ?: 0) > 200) {
                            TextButton(
                                onClick = { isInstructionsExpanded = !isInstructionsExpanded },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(
                                    text = if (isInstructionsExpanded) "Show Less" else "Show More",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}