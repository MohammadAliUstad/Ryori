package com.yugentech.ryori.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Suppress("DEPRECATION")
@Composable
fun SearchBar(
    navController: NavController,
    searchText: TextFieldValue,
    onSearchTextChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        label = {
            Text(
                "Search",
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        },
        placeholder = {
            Text(
                "Search meals or categories",
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        },
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .focusRequester(focusRequester),
        shape = RoundedCornerShape(50),
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledTextColor = MaterialTheme.colorScheme.onBackground,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
            cursorColor = MaterialTheme.colorScheme.onBackground
        ),
        singleLine = true,
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                val query = searchText.text.toLowerCase().trim()
                if (query.isNotEmpty()) {
                    navController.navigate("meal/$query")
                }
            }
        )
    )
}