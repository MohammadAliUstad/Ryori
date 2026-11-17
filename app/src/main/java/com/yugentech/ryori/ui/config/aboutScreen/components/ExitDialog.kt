package com.yugentech.ryori.ui.config.aboutScreen.components

import com.yugentech.ryori.R
import androidx.compose.ui.res.stringResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.runtime.Composable
import com.yugentech.ryori.ui.main.mainScreen.components.RyoriDialog

@Composable
fun ExitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    RyoriDialog(
        icon = Icons.AutoMirrored.Outlined.ExitToApp,
        title = stringResource(R.string.exit_title),
        message = stringResource(R.string.exit_message),
        confirmLabel = stringResource(R.string.settings_exit),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}
