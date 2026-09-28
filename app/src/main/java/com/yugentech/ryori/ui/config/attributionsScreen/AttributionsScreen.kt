package com.yugentech.ryori.ui.config.attributionsScreen

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import com.yugentech.ryori.theme.tokens.LocalDesignTokens
import com.yugentech.ryori.theme.tokens.spacing
import com.yugentech.ryori.ui.main.mainScreen.components.SectionHeader
import com.yugentech.ryori.ui.config.aboutScreen.model.license.LicensesContent
import com.yugentech.ryori.ui.main.mainScreen.components.itemShape
import com.yugentech.ryori.utils.AppConstants
import com.yugentech.ryori.ui.config.attributionsScreen.components.AttributionCarousel
import com.yugentech.ryori.ui.config.attributionsScreen.components.AttributionsTopBar
import com.yugentech.ryori.ui.config.attributionsScreen.components.LibraryItem
import com.yugentech.ryori.ui.config.attributionsScreen.components.OpenSourceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttributionsScreen(
    onNavigateBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val libraries = remember { LicensesContent.libraries }
    val layoutDirection = LocalLayoutDirection.current

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AttributionsTopBar(
                scrollBehavior = scrollBehavior,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { scaffoldPadding ->
        val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
        val tokens = LocalDesignTokens.current

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = scaffoldPadding.calculateTopPadding()),
            contentPadding = PaddingValues(
                top = tokens.spacing.m,
                bottom = navBarPadding.calculateBottomPadding(),
                start = tokens.spacing.m + scaffoldPadding.calculateStartPadding(layoutDirection),
                end = tokens.spacing.m + scaffoldPadding.calculateEndPadding(layoutDirection)
            ),
            verticalArrangement = Arrangement.spacedBy(tokens.spacing.xxs)
        ) {
            item { OpenSourceCard(githubUrl = AppConstants.GITHUB_URL) }

            item { AttributionCarousel() }

            item {
                SectionHeader(
                    title = "Libraries We Use",
                    icon = Icons.Default.IntegrationInstructions
                )
            }

            itemsIndexed(libraries) { index, lib ->
                LibraryItem(
                    lib = lib,
                    shape = itemShape(index, libraries.size)
                )
            }
        }
    }
}