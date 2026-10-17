package com.yugentech.ryori.ui.config.settingsScreen.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yugentech.ryori.data.settings.SettingsRepository
import com.yugentech.ryori.theme.tokens.corners
import com.yugentech.ryori.ui.main.mainScreen.components.RyoriDialog
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

// --- Name --------------------------------------------------------------------------------

// Same sheet Quill uses to rename a category: drag handle, bold title, a filled text field
// that gets focus once the sheet is open, and Cancel / Save aligned to the end.
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditNameSheet(
    currentName: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = currentName,
                selection = TextRange(currentName.length)
            )
        )
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusRequester = remember { FocusRequester() }

    val currentText = textFieldValue.text
    val canSave = currentText.isNotBlank() && currentText.trim() != currentName

    // Save eases between its disabled and enabled colours instead of snapping, on the theme's
    // effects spring (no bounce). Both states get the same animated colour, so the button's own
    // enabled/disabled switch can't cut the animation short.
    val defaultSaveColors = ButtonDefaults.buttonColors()
    val colorSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>()
    val saveContainerColor by animateColorAsState(
        targetValue = if (canSave) defaultSaveColors.containerColor else defaultSaveColors.disabledContainerColor,
        animationSpec = colorSpec,
        label = "saveContainer"
    )
    val saveContentColor by animateColorAsState(
        targetValue = if (canSave) defaultSaveColors.contentColor else defaultSaveColors.disabledContentColor,
        animationSpec = colorSpec,
        label = "saveContent"
    )

    val save = {
        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
        onSave(currentText.trim())
    }

    LaunchedEffect(Unit) {
        snapshotFlow { sheetState.currentValue }
            .filter { it == SheetValue.Expanded }
            .first()
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
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
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Change Your Name",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextField(
                value = textFieldValue,
                onValueChange = {
                    if (it.text.length <= SettingsRepository.MAX_NAME_LENGTH) textFieldValue = it
                },
                label = { Text("Chef Name") },
                supportingText = {
                    Text(
                        text = "${currentText.length} / ${SettingsRepository.MAX_NAME_LENGTH}",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                shape = RoundedCornerShape(MaterialTheme.corners.medium),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { if (canSave) save() })
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // No haptic on Cancel: like every dialog, only the action button gives feedback.
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = save,
                    enabled = canSave,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = saveContainerColor,
                        contentColor = saveContentColor,
                        disabledContainerColor = saveContainerColor,
                        disabledContentColor = saveContentColor
                    )
                ) { Text("Save") }
            }
        }
    }
}

// --- Confirmations ---------------------------------------------------------------------------

// Confirms an action, then closes itself. The same dialog Sessions uses to delete a session or
// template: a plain Material alert with the title and message at the start, and a text "Cancel"
// beside a solid action button at the end. destructive = true (clear, delete) makes the action red.
// The action button gives a Confirm haptic; Cancel and tapping outside give none.
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    destructive: Boolean = false
) {
    val haptic = LocalHapticFeedback.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = MaterialTheme.typography.headlineSmall) },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        shape = RoundedCornerShape(MaterialTheme.corners.large),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        confirmButton = {
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                    onConfirm()
                    onDismiss()
                },
                colors = if (destructive) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                } else {
                    ButtonDefaults.buttonColors()
                }
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
