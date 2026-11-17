package com.yugentech.ryori.ui.config.moreAppsScreen

import androidx.compose.ui.res.stringResource
import androidx.annotation.StringRes
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.outlined.CallSplit
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.KeyboardVoice
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.yugentech.ryori.theme.SheetTheme
import com.yugentech.ryori.theme.tokens.spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class Feature(
    val icon: ImageVector,
    @param:StringRes val title: Int,
    @param:StringRes val description: Int
)

private data class Fact(val value: String, @param:StringRes val label: Int)

private data class SisterApp(
    val name: String,
    @param:StringRes val tagline: Int,
    val packageName: String,
    @param:DrawableRes val animatedIcon: Int,
    // Launcher background colour, so the icon sits on the same tile as on the home screen.
    val iconBackground: Color,
    // The launcher-style vectors have padding baked in, so each one is drawn larger than its
    // tile and clipped. Tuned per icon.
    val iconSize: Dp,
    val animationMillis: Long,
    val facts: List<Fact>,
    val features: List<Feature>,
    // Not out yet: a Coming soon label stands in for the Play Store buttons.
    val comingSoon: Boolean = false
)

private val quill = SisterApp(
    name = "Quill",
    tagline = R.string.quill_tagline,
    packageName = "com.yugentech.quill",
    animatedIcon = R.drawable.avd_quill,
    iconBackground = Color(0xFF576421),
    iconSize = 64.dp,
    animationMillis = 1900,
    facts = listOf(
        Fact("4.9★", R.string.more_apps_fact_reviews_quill),
        Fact("1K+", R.string.more_apps_fact_downloads)
    ),
    features = listOf(
        Feature(
            Icons.Outlined.Psychology,
            R.string.quill_feature_1_title,
            R.string.quill_feature_1_body
        ),
        Feature(
            Icons.Outlined.AutoAwesome,
            R.string.quill_feature_2_title,
            R.string.quill_feature_2_body
        ),
        Feature(
            Icons.Outlined.Image,
            R.string.quill_feature_3_title,
            R.string.quill_feature_3_body
        ),
        Feature(
            Icons.Outlined.AutoStories,
            R.string.quill_feature_4_title,
            R.string.quill_feature_4_body
        ),
        Feature(
            Icons.Outlined.LocalLibrary,
            R.string.quill_feature_5_title,
            R.string.quill_feature_5_body
        ),
        Feature(
            Icons.Outlined.Insights,
            R.string.quill_feature_6_title,
            R.string.quill_feature_6_body
        )
    )
)

private val sessions = SisterApp(
    name = "Sessions",
    tagline = R.string.sessions_tagline,
    packageName = "com.yugentech.sessions",
    animatedIcon = R.drawable.ic_sessions_animated,
    iconBackground = Color(0xFFFFE6C7),
    iconSize = 156.dp,
    animationMillis = 900,
    facts = listOf(
        Fact("4.9★", R.string.more_apps_fact_reviews),
        Fact("5K+", R.string.more_apps_fact_downloads)
    ),
    features = listOf(
        Feature(
            Icons.Outlined.Timer,
            R.string.sessions_feature_1_title,
            R.string.sessions_feature_1_body
        ),
        Feature(
            Icons.Outlined.Headphones,
            R.string.sessions_feature_2_title,
            R.string.sessions_feature_2_body
        ),
        Feature(
            Icons.Outlined.Analytics,
            R.string.sessions_feature_3_title,
            R.string.sessions_feature_3_body
        ),
        Feature(
            Icons.Outlined.Security,
            R.string.sessions_feature_4_title,
            R.string.sessions_feature_4_body
        ),
        Feature(
            Icons.Outlined.ColorLens,
            R.string.sessions_feature_5_title,
            R.string.sessions_feature_5_body
        ),
        Feature(
            Icons.Outlined.History,
            R.string.sessions_feature_6_title,
            R.string.sessions_feature_6_body
        )
    )
)

private val mynt = SisterApp(
    name = "Mynt",
    tagline = R.string.mynt_tagline,
    packageName = "com.yugentech.mynt",
    animatedIcon = R.drawable.avd_mynt,
    iconBackground = Color(0xFFFBF6E4),
    // Drawn larger than the tile, as Mynt itself does, so the wallet fills it.
    iconSize = 132.dp,
    animationMillis = 1300,
    // Out on the Play Store now, so it gets the Get on Play Store button (no store stats yet)
    facts = emptyList(),
    // Mynt's own key features, from its Meet Mynt screen.
    features = listOf(
        Feature(
            Icons.Outlined.KeyboardVoice,
            R.string.mynt_feature_1_title,
            R.string.mynt_feature_1_body
        ),
        Feature(
            Icons.Outlined.Handshake,
            R.string.mynt_feature_2_title,
            R.string.mynt_feature_2_body
        ),
        Feature(
            Icons.AutoMirrored.Outlined.CallSplit,
            R.string.mynt_feature_3_title,
            R.string.mynt_feature_3_body
        ),
        Feature(
            Icons.Outlined.QueryStats,
            R.string.mynt_feature_4_title,
            R.string.mynt_feature_4_body
        ),
        Feature(
            Icons.Outlined.EditNote,
            R.string.mynt_feature_5_title,
            R.string.mynt_feature_5_body
        ),
        Feature(
            Icons.Outlined.RecordVoiceOver,
            R.string.mynt_feature_6_title,
            R.string.mynt_feature_6_body
        )
    )
)

private val hajime = SisterApp(
    name = "Hajime",
    tagline = R.string.hajime_tagline,
    packageName = "com.yugentech.hajime",
    animatedIcon = R.drawable.avd_hajime,
    iconBackground = Color(0xFFF5F0E8),
    iconSize = 156.dp,
    animationMillis = 1660,
    // Not on the store yet, so its card has no stats strip.
    facts = emptyList(),
    features = listOf(
        Feature(
            Icons.Outlined.Translate,
            R.string.hajime_feature_1_title,
            R.string.hajime_feature_1_body
        ),
        Feature(
            Icons.Outlined.School,
            R.string.hajime_feature_2_title,
            R.string.hajime_feature_2_body
        ),
        Feature(
            Icons.Outlined.History,
            R.string.hajime_feature_3_title,
            R.string.hajime_feature_3_body
        ),
        Feature(
            Icons.Outlined.AutoAwesome,
            R.string.hajime_feature_4_title,
            R.string.hajime_feature_4_body
        ),
        Feature(
            Icons.Outlined.RecordVoiceOver,
            R.string.hajime_feature_5_title,
            R.string.hajime_feature_5_body
        ),
        Feature(
            Icons.Outlined.Insights,
            R.string.hajime_feature_6_title,
            R.string.hajime_feature_6_body
        )
    )
)

// The other apps, in the family's order, as the screen and the sheet list them.
private val sisterApps = listOf(quill, mynt, hajime, sessions)

// Where each app's carousel starts in the primary / secondary / tertiary cycle.
private val CarouselColorOffsets = listOf(0, 1, 2, 0)

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
                title = { Text(stringResource(R.string.settings_more_from_us)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
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
                bottom = navBarPadding.calculateBottomPadding() + 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l)
        ) {
            item { IntroText() }

            sisterApps.forEachIndexed { appIndex, app ->
                item(key = "${app.name}_hero") {
                    AppHeroCard(app = app, onGetApp = { openStore(context, app.packageName) })
                }
                item(key = "${app.name}_features") {
                    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)) {
                        SectionLabel(stringResource(R.string.more_apps_key_features))
                        // Each carousel starts on the next colour along, so neighbours don't repeat.
                        FeatureCarousel(features = app.features, colorOffset = CarouselColorOffsets[appIndex])
                    }
                }
                // Between the apps, and between the last app and the closing card.
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
            text = stringResource(R.string.more_apps_subtitle),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.more_apps_intro),
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
                            text = stringResource(app.tagline),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (app.comingSoon) {
                        ComingSoonLabel()
                    } else {
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
                                text = stringResource(R.string.more_apps_get_on_play),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
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
            text = stringResource(fact.label),
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
                        text = stringResource(feature.title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = fg
                    )
                    Text(
                        text = stringResource(feature.description),
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
                OverviewIcon(icon = Icons.Outlined.AccountBalanceWallet, contentDescription = "Mynt")
                OverviewIcon(icon = Icons.Outlined.Translate, contentDescription = "Hajime")
                OverviewIcon(icon = Icons.Outlined.Timer, contentDescription = "Sessions")
            }
            Text(
                text = stringResource(R.string.more_apps_overview_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.more_apps_overview_body),
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

// ============================== Sheet ==============================

/**
 * The short version of this screen, opened from the More tab's About section: each app's icon,
 * name and tagline with a Play Store button, then a link to the full screen. Tapping an icon
 * plays its animation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreFromUsSheet(onDismiss: () -> Unit, onSeeAll: () -> Unit) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val apps = sisterApps

    SheetTheme {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            // Sessions' sheet handle: a small pill with roomy padding, so it's easy to grab.
            dragHandle = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                        .padding(vertical = 22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 32.dp, height = 6.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                shape = CircleShape
                            )
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_more_from_us),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.more_apps_sheet_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Column(
                    modifier = Modifier.padding(top = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    apps.forEachIndexed { index, app ->
                        SheetAppRow(
                            name = app.name,
                            tagline = stringResource(app.tagline),
                            iconBackground = app.iconBackground,
                            animationMillis = app.animationMillis,
                            index = index,
                            count = apps.size,
                            onGetApp = if (app.comingSoon) null else { { openStore(context, app.packageName) } }
                        ) { isAnimating ->
                            AnimatedAppIcon(
                                drawableRes = app.animatedIcon,
                                isAnimating = isAnimating,
                                // Scaled from the full screen's 88dp tile to the sheet's 56dp one.
                                modifier = Modifier.requiredSize(app.iconSize * (56f / 88f))
                            )
                        }
                    }
                }

                TextButton(
                    onClick = {
                        // Slides the sheet away first, so it doesn't vanish under the new screen.
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            onDismiss()
                            onSeeAll()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 12.dp)
                ) {
                    Text(stringResource(R.string.more_apps_see_all))
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SheetAppRow(
    name: String,
    tagline: String,
    iconBackground: Color,
    animationMillis: Long,
    index: Int,
    count: Int,
    onGetApp: (() -> Unit)?,
    icon: @Composable (isAnimating: Boolean) -> Unit
) {
    var isAnimating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val play: () -> Unit = {
        if (!isAnimating) {
            isAnimating = true
            scope.launch {
                delay(animationMillis)
                isAnimating = false
            }
        }
    }
    val large = 20.dp
    val small = 6.dp
    Surface(
        shape = RoundedCornerShape(
            topStart = if (index == 0) large else small,
            topEnd = if (index == 0) large else small,
            bottomStart = if (index == count - 1) large else small,
            bottomEnd = if (index == count - 1) large else small
        ),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(iconBackground)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = play
                    )
            ) {
                icon(isAnimating)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = tagline,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (onGetApp == null) {
                ComingSoonLabel()
            } else {
                FilledTonalButton(
                    onClick = onGetApp,
                    contentPadding = PaddingValues(horizontal = 18.dp)
                ) {
                    Text(stringResource(R.string.more_apps_get))
                }
            }
        }
    }
}

// Stands in for the Play Store button until an app is out.
@Composable
private fun ComingSoonLabel() {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(
            text = stringResource(R.string.more_apps_coming_soon),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

// Play Store app if installed, otherwise the store's web page.
private fun openStore(context: Context, packageName: String) {
    val market = Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri())
    val web = Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$packageName".toUri())
    runCatching { context.startActivity(market) }
        .onFailure { runCatching { context.startActivity(web) } }
}
