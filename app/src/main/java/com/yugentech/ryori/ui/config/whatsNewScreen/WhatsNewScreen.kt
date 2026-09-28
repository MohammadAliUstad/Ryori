package com.yugentech.ryori.ui.config.whatsNewScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.EmojiPeople
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.LocalBar
import androidx.compose.material.icons.rounded.NewReleases
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.TravelExplore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.ryori.theme.tokens.corners
import com.yugentech.ryori.theme.tokens.spacing
import com.yugentech.ryori.ui.main.mainScreen.components.itemShape

private data class WhatsNewItem(
    val icon: ImageVector,
    val title: String,
    val body: String
)

private val whatsNew = listOf(
    WhatsNewItem(
        Icons.Rounded.Home,
        "A Home that inspires",
        "A featured carousel, a cuisine and a category of the day, and a Surprise me button for when you can't decide."
    ),
    WhatsNewItem(
        Icons.Rounded.TravelExplore,
        "Explore every kitchen",
        "Browse hundreds of recipes by category, cuisine or ingredient, or search for one by name."
    ),
    WhatsNewItem(
        Icons.Rounded.Checklist,
        "Cook along",
        "Clear step-by-step instructions, with ingredients you can tick off as you go."
    ),
    WhatsNewItem(
        Icons.Rounded.LocalBar,
        "Mocktails",
        "Non-alcoholic drinks sit right next to the meals, so there's something for every glass."
    ),
    WhatsNewItem(
        Icons.Rounded.Eco,
        "Vegetarian mode",
        "One switch hides meat and seafood across Home, Explore, search and suggestions."
    ),
    WhatsNewItem(
        Icons.Rounded.EmojiPeople,
        "Your chef profile",
        "Set your name and watch your kitchen stats grow as you cook."
    ),
    WhatsNewItem(
        Icons.Rounded.History,
        "Recently viewed",
        "Every recipe you open is kept, so the one from last night is always a tap away."
    ),
    WhatsNewItem(
        Icons.Rounded.LightMode,
        "Keep screen on",
        "No more tapping the screen with floury fingers while you follow a recipe."
    ),
    WhatsNewItem(
        Icons.Rounded.CloudOff,
        "Works offline",
        "Recipes you've opened are saved on your device, so they load instantly, even without a connection."
    ),
    WhatsNewItem(
        Icons.Rounded.Palette,
        "Make it yours",
        "Themes, colors and gentle haptics to make Ryori feel like your own kitchen."
    )
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WhatsNewScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val versionName = remember(context) {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }
            .getOrNull()
    }

    // A few expressive shapes, cycled through so the list doesn't look like a column of dots.
    val shapes = listOf(
        MaterialShapes.Cookie9Sided.toShape(),
        MaterialShapes.Clover4Leaf.toShape(),
        MaterialShapes.Sunny.toShape(),
        MaterialShapes.Pill.toShape()
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("What's There")
                        Text(
                            text = versionName?.let { "Version $it" } ?: "Everything Ryori can do",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = MaterialTheme.spacing.m,
                end = MaterialTheme.spacing.m,
                top = innerPadding.calculateTopPadding(),
                bottom = navBarPadding.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = MaterialTheme.spacing.s)
                        .clip(RoundedCornerShape(MaterialTheme.corners.extraLarge))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(MaterialTheme.spacing.m)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.NewReleases,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = "Welcome to Ryori",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Here's everything you can cook up with Ryori.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            itemsIndexed(whatsNew) { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(itemShape(index, whatsNew.size))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(MaterialTheme.spacing.m)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(shapes[index % shapes.size])
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = item.body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
