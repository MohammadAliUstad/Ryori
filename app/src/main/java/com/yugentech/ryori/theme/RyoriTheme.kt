package com.yugentech.ryori.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.yugentech.ryori.theme.builder.getColorScheme
import com.yugentech.ryori.theme.builder.getFontFamily
import com.yugentech.ryori.theme.builder.getTypography
import com.yugentech.ryori.theme.config.ThemeConfiguration
import com.yugentech.ryori.theme.tokens.LocalDesignTokens
import com.yugentech.ryori.theme.tokens.TokensCompact

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RyoriTheme(
    themeConfiguration: ThemeConfiguration,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(
        themeConfiguration = themeConfiguration
    )

    val currentTypography = remember(themeConfiguration.appFont) {
        getTypography(getFontFamily(themeConfiguration.appFont))
    }

    val tokens = TokensCompact

    CompositionLocalProvider(LocalDesignTokens provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = currentTypography,
            content = content,
            motionScheme = MotionScheme.expressive()
        )
    }
}