package com.yugentech.ryori.ui.config.aboutScreen.about

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.StarRate
import androidx.core.net.toUri
import com.yugentech.ryori.utils.AppConstants

object AboutContent {

    fun getSupportItems(
        context: Context,
        onDonateClick: () -> Unit
    ): List<AboutOption> {
        return listOf(
            AboutOption(
                title = "Contact Developer",
                subtitle = "Get in touch for support",
                icon = Icons.Default.Email,
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO, AppConstants.SUPPORT_EMAIL.toUri())
                    context.startActivity(intent)
                }
            ),
            AboutOption(
                title = "Buy me a coffee",
                subtitle = "Support the development",
                icon = Icons.Default.LocalCafe,
                onClick = onDonateClick
            )
        )
    }

    fun getCommunityItems(context: Context): List<AboutOption> {
        return listOf(
            AboutOption(
                title = "Rate this App",
                subtitle = "Leave a review on the Play Store",
                icon = Icons.Default.StarRate,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, AppConstants.PLAY_STORE_URL.toUri())
                    context.startActivity(intent)
                }
            )
        )
    }
}