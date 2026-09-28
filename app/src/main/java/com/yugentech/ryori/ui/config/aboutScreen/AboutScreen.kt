package com.yugentech.ryori.ui.config.aboutScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import com.yugentech.ryori.theme.tokens.spacing
import com.yugentech.ryori.ui.config.aboutScreen.components.AppInfoCard
import com.yugentech.ryori.ui.config.aboutScreen.about.AboutContent
import com.yugentech.ryori.ui.main.mainScreen.components.SectionHeader
import com.yugentech.ryori.ui.config.settingsScreen.components.SettingsListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLicenses: () -> Unit,
    onNavigateToMoreApps: () -> Unit
) {
    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val supportItems = remember(context) {
        AboutContent.getSupportItems(
            context = context,
            onMoreAppsClick = onNavigateToMoreApps
        )
    }

    val communityItems = remember(context) {
        AboutContent.getCommunityItems(context)
    }

    val legalItems = remember(context) {
        AboutContent.getLegalItems(
            context = context,
            onNavigateToLicenses = onNavigateToLicenses
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("About")
                        Text(
                            "App information and credits",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { scaffoldPadding ->
        val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = scaffoldPadding.calculateTopPadding())
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs),
                contentPadding = PaddingValues(
                    bottom = navBarPadding.calculateBottomPadding(),
                    start = MaterialTheme.spacing.m + scaffoldPadding.calculateStartPadding(layoutDirection),
                    end = MaterialTheme.spacing.m + scaffoldPadding.calculateEndPadding(layoutDirection)
                )
            ) {
                item { AppInfoCard() }

                item { SectionHeader(Icons.Filled.Favorite, "Connect & Support") }
                itemsIndexed(supportItems) { index, item ->
                    SettingsListItem(
                        title = item.title,
                        subtitle = item.subtitle,
                        leadingIcon = item.icon,
                        index = index,
                        totalCount = supportItems.size,
                        onClick = item.onClick
                    )
                }

                item { SectionHeader(Icons.Filled.ThumbUp, "Spread the Word") }
                itemsIndexed(communityItems) { index, item ->
                    SettingsListItem(
                        title = item.title,
                        subtitle = item.subtitle,
                        leadingIcon = item.icon,
                        index = index,
                        totalCount = communityItems.size,
                        onClick = item.onClick
                    )
                }

                item { SectionHeader(Icons.Filled.Info, "Legal") }
                itemsIndexed(legalItems) { index, item ->
                    SettingsListItem(
                        title = item.title,
                        subtitle = item.subtitle,
                        leadingIcon = item.icon,
                        index = index,
                        totalCount = legalItems.size,
                        onClick = item.onClick
                    )
                }
            }
        }
    }
}
