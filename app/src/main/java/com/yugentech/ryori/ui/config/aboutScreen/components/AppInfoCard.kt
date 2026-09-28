package com.yugentech.ryori.ui.config.aboutScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yugentech.ryori.R
import com.yugentech.ryori.theme.tokens.components
import com.yugentech.ryori.theme.tokens.corners
import com.yugentech.ryori.theme.tokens.spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Matches avd_ryori_burger's full build-up (~1.12s), rounded up. The icon rests on the
// assembled burger and only animates when tapped.
private const val BURGER_ANIMATION_MS = 1200L

@Composable
fun AppInfoCard() {
    var isAnimating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun playBurger() {
        if (isAnimating) return
        isAnimating = true
        scope.launch {
            delay(BURGER_ANIMATION_MS)
            isAnimating = false
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(MaterialTheme.corners.medium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = MaterialTheme.spacing.xl,
                    horizontal = MaterialTheme.spacing.l
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .size(MaterialTheme.components.imageSizeMedium)
                    // The icon on its launcher background, same tile as on the More from us screen.
                    .clip(RoundedCornerShape(24.dp))
                    .background(colorResource(R.color.ic_launcher_background))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { playBurger() },
                contentAlignment = Alignment.Center
            ) {
                AnimatedRyoriIcon(
                    isAnimating = isAnimating,
                    modifier = Modifier.requiredSize(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.l))

            Text(
                text = "Ryori",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

            Text(
                text = "Ryori makes every meal feel like an adventure. Discover dishes from around the world, cook along step by step, and pour a mocktail while you're at it.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.s)
            )
        }
    }
}
