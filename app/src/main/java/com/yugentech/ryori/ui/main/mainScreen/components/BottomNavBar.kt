package com.yugentech.ryori.ui.main.mainScreen.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.yugentech.ryori.navigation.screen.BottomBarScreen

// Same setup as Quill's BottomBar: default Material NavigationBar colors and label styling,
// and re-tapping the current tab does nothing. Quill swaps in animated vector icons here;
// Ryori uses its filled/outlined icon pairs until it has AVDs of its own.
@Composable
fun BottomNavBar(
    currentTab: BottomBarScreen,
    onTabSelected: (BottomBarScreen) -> Unit
) {
    NavigationBar {
        BottomBarScreen.all.forEach { screen ->
            val isSelected = currentTab == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { if (!isSelected) onTabSelected(screen) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) }
            )
        }
    }
}
