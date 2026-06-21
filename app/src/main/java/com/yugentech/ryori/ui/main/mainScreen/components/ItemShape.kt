package com.yugentech.ryori.ui.main.mainScreen.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import com.yugentech.ryori.theme.tokens.corners

@Composable
fun itemShape(index: Int, count: Int): Shape {
    val largeCorner = MaterialTheme.corners.large
    val smallCorner = MaterialTheme.corners.small

    return when {
        count == 1 -> RoundedCornerShape(largeCorner)
        index == 0 -> RoundedCornerShape(
            topStart = largeCorner,
            topEnd = largeCorner,
            bottomStart = smallCorner,
            bottomEnd = smallCorner
        )

        index == count - 1 -> RoundedCornerShape(
            topStart = smallCorner,
            topEnd = smallCorner,
            bottomStart = largeCorner,
            bottomEnd = largeCorner
        )

        else -> RoundedCornerShape(smallCorner)
    }
}

// Horizontal counterpart of itemShape (same as Quill's insights row): a row of cards reads as
// one grouped block, with large corners only on the outer edges.
@Composable
fun rowItemShape(index: Int, count: Int): Shape {
    val largeCorner = MaterialTheme.corners.large
    val smallCorner = MaterialTheme.corners.small

    return when {
        count == 1 -> RoundedCornerShape(largeCorner)
        index == 0 -> RoundedCornerShape(
            topStart = largeCorner,
            bottomStart = largeCorner,
            topEnd = smallCorner,
            bottomEnd = smallCorner
        )

        index == count - 1 -> RoundedCornerShape(
            topStart = smallCorner,
            bottomStart = smallCorner,
            topEnd = largeCorner,
            bottomEnd = largeCorner
        )

        else -> RoundedCornerShape(smallCorner)
    }
}