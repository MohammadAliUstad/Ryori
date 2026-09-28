package com.yugentech.ryori.ui.config.moreAppsScreen

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.yugentech.ryori.R
import com.yugentech.ryori.theme.tokens.spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class Feature(
    val icon: ImageVector,
    val title: String,
    val description: String
)

private data class Fact(val value: String, val label: String)

private data class SisterApp(
    val name: String,
    val tagline: String,
    val packageName: String,
    @param:DrawableRes val animatedIcon: Int,
    // Launcher background colour, so the icon sits on the same tile as on the home screen.
    val iconBackground: Color,
    // The launcher-style vectors have padding baked in, so each one is drawn larger than its
    // tile and clipped. Tuned per icon.
    val iconSize: Dp,
    val animationMillis: Long,
    val facts: List<Fact>,
    val features: List<Feature>
)

private val quill = SisterApp(
    name = "Quill",
    tagline = "Read Deeper. Think Further.",
    packageName = "com.yugentech.quill",
    animatedIcon = R.drawable.avd_quill,
    iconBackground = Color(0xFF576421),
    iconSize = 64.dp,
    animationMillis = 1900,
    // No store stats for Quill yet, so its card has no stats strip.
    facts = emptyList(),
    features = listOf(
        Feature(
            Icons.Outlined.Psychology,
            "Aira",
            "An AI reading companion that answers from your book, locked to your progress so it never spoils what's next."
        ),
        Feature(
            Icons.Outlined.AutoAwesome,
            "Character Companion",
            "Recall who someone is, what they're up to lately, or trace their whole journey so far."
        ),
        Feature(
            Icons.Outlined.Image,
            "Visualize Scenes",
            "Turn a passage into an illustration, then jump straight back to where it came from."
        ),
        Feature(
            Icons.Outlined.AutoStories,
            "A Beautiful Reader",
            "Fonts, spacing, page themes, highlights, night light and paged or scrolling layouts."
        ),
        Feature(
            Icons.Outlined.LocalLibrary,
            "Endless Library",
            "Import your own EPUBs or download from 60,000+ free Project Gutenberg and Standard Ebooks titles."
        ),
        Feature(
            Icons.Outlined.Insights,
            "Reading Insights",
            "Streaks, a reading heatmap, peak hours and favorite authors, with gentle daily reminders."
        )
    )
)

private val sessions = SisterApp(
    name = "Sessions",
    tagline = "Ultimate Pomodoro Timer",
    packageName = "com.yugentech.sessions",
    animatedIcon = R.drawable.ic_sessions_animated,
    iconBackground = Color(0xFFFFE6C7),
    iconSize = 156.dp,
    animationMillis = 900,
    facts = listOf(
        Fact("4.9★", "70+ reviews"),
        Fact("3K+", "Downloads")
    ),
    features = listOf(
        Feature(
            Icons.Outlined.Timer,
            "Smart Engine",
            "Customizable cycles and smart intervals that work out when to trigger long breaks."
        ),
        Feature(
            Icons.Outlined.Headphones,
            "Immersive Audio",
            "6 curated ambient sounds with adaptive ducking and haptic feedback."
        ),
        Feature(
            Icons.Outlined.Analytics,
            "Deep Analytics",
            "Heatmaps, total focus time and your peak productivity hours at a glance."
        ),
        Feature(
            Icons.Outlined.Security,
            "Unkillable",
            "Built to resist aggressive battery optimization, so your timer never drops."
        ),
        Feature(
            Icons.Outlined.ColorLens,
            "Deep Theming",
            "8 color themes, dynamic Material You, OLED black mode and 6 font options."
        ),
        Feature(
            Icons.Outlined.History,
            "Task History",
            "Name your sessions to track and review exactly what you worked on."
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreAppsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("More from us") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + MaterialTheme.spacing.s,
                bottom = navBarPadding.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l)
        ) {
            item { IntroText() }

            listOf(quill, sessions).forEachIndexed { appIndex, app ->
                item(key = "${app.name}_hero") {
                    AppHeroCard(app = app, onGetApp = { openStore(context, app.packageName) })
                }
                item(key = "${app.name}_features") {
                    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)) {
                        SectionLabel("Key Features")
                        // The second app starts at tertiary so the two carousels don't repeat the same colours.
                        FeatureCarousel(features = app.features, colorOffset = if (appIndex == 0) 0 else 2)
                    }
                }
                // Between the two apps, and between the second app and the closing card.
                item(key = "${app.name}_divider") { SectionDivider() }
            }

            item { ClosingCard() }
        }
    }
}

@Composable
private fun IntroText() {
    Column(
        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.m),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
    ) {
        Text(
            text = "Two more apps, made with the same care",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "If you enjoy cooking with Ryori, you might like reading with Quill and focusing with Sessions.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// A quiet break between sections: two hairlines with a small star in the middle.
@Composable
private fun SectionDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Text(
            text = "✦",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.outline
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.m)
    )
}

@Composable
private fun AppHeroCard(app: SisterApp, onGetApp: () -> Unit) {
    var isAnimating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val replay = {
        if (!isAnimating) {
            isAnimating = true
            scope.launch {
                delay(app.animationMillis)
                isAnimating = false
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.m),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.m),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
            ) {
                // Tap the icon to replay its animation, like the About card.
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(app.iconBackground)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = replay
                        )
                ) {
                    AnimatedAppIcon(
                        drawableRes = app.animatedIcon,
                        isAnimating = isAnimating,
                        modifier = Modifier.requiredSize(app.iconSize)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column {
                        Text(
                            text = app.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = app.tagline,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onGetApp,
                        modifier = Modifier.height(40.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Get on Play Store",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }

            if (app.facts.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.m),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // Play Store-style strip of quick facts.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    app.facts.forEachIndexed { index, fact ->
                        if (index > 0) {
                            VerticalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                        FactItem(fact = fact, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun FactItem(fact: Fact, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = fact.value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = fact.label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeatureCarousel(features: List<Feature>, colorOffset: Int = 0) {
    val containerColors = listOf(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer
    )
    val contentColors = listOf(
        MaterialTheme.colorScheme.onPrimaryContainer,
        MaterialTheme.colorScheme.onSecondaryContainer,
        MaterialTheme.colorScheme.onTertiaryContainer
    )

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { features.size },
        preferredItemWidth = 256.dp,
        itemSpacing = 12.dp,
        contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.m),
        modifier = Modifier.fillMaxWidth()
    ) { index ->
        val feature = features[index]
        val colorIndex = (index + colorOffset) % containerColors.size
        val bg = containerColors[colorIndex]
        val fg = contentColors[colorIndex]

        Card(
            modifier = Modifier
                .height(196.dp)
                .maskClip(MaterialTheme.shapes.extraLarge),
            colors = CardDefaults.cardColors(containerColor = bg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = null,
                    tint = fg,
                    modifier = Modifier.size(30.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = feature.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = fg
                    )
                    Text(
                        text = feature.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = fg.copy(alpha = 0.8f),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ClosingCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.m),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // One simple icon per app, in the same order as the headline below.
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OverviewIcon(icon = Icons.Outlined.LunchDining, contentDescription = "Ryori")
                OverviewIcon(icon = Icons.Outlined.AutoStories, contentDescription = "Quill")
                OverviewIcon(icon = Icons.Outlined.Timer, contentDescription = "Sessions")
            }
            Text(
                text = "Cook. Read. Focus.",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Ryori, Quill and Sessions are made by YugenTech: small, thoughtful apps built to make everyday moments a little calmer.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun OverviewIcon(icon: ImageVector, contentDescription: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(26.dp)
        )
    }
}

// Same approach as the About card's burger: an AnimatedVectorDrawable in an ImageView,
// restarted whenever isAnimating flips to true.
@Composable
private fun AnimatedAppIcon(
    @DrawableRes drawableRes: Int,
    isAnimating: Boolean,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            ImageView(ctx).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageResource(drawableRes)
            }
        },
        update = { imageView ->
            if (isAnimating) {
                (imageView.drawable as? Animatable)?.start()
            }
        }
    )
}

// Play Store app if installed, otherwise the store's web page.
private fun openStore(context: Context, packageName: String) {
    val market = Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri())
    val web = Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$packageName".toUri())
    runCatching { context.startActivity(market) }
        .onFailure { runCatching { context.startActivity(web) } }
}
