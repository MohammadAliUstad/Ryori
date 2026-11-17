package com.yugentech.ryori.ui.config.aboutScreen.about

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class AboutOption(
    @param:StringRes val title: Int,
    @param:StringRes val subtitle: Int?,
    val icon: ImageVector,
    val onClick: () -> Unit
)