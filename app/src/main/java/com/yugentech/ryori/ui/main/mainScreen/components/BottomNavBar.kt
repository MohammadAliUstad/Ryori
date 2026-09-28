package com.yugentech.ryori.ui.main.mainScreen.components

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.yugentech.ryori.R
import com.yugentech.ryori.navigation.screen.BottomBarScreen

// Same setup as Quill's BottomBar: default Material NavigationBar colors and label styling,
// and re-tapping the current tab does nothing. Home and Search use Lordicon Lottie icons that
// play their "pinch" once when the tab is selected; More uses Quill's animated More icon.
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
                    when (screen) {
                        BottomBarScreen.Home -> LottieNavIcon(
                            rawRes = R.raw.lottie_nav_home,
                            selected = isSelected,
                            contentDescription = screen.title
                        )

                        BottomBarScreen.Search -> LottieNavIcon(
                            rawRes = R.raw.lottie_nav_search,
                            selected = isSelected,
                            contentDescription = screen.title
                        )

                        BottomBarScreen.More -> AnimatedNavIcon(
                            avdRes = R.drawable.avd_nav_more,
                            selected = isSelected,
                            contentDescription = screen.title
                        )
                    }
                },
                label = { Text(screen.title) }
            )
        }
    }
}

// Lordicon exports are drawn in one dark colour; every fill and stroke is re-tinted with the
// colour NavigationBarItem gives its icon, so it follows the theme and the selected state.
// The animation starts and ends on the same resting pose, so it's played once on selection.
@Composable
private fun LottieNavIcon(
    @RawRes rawRes: Int,
    selected: Boolean,
    contentDescription: String
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(rawRes))
    val animatable = rememberLottieAnimatable()

    LaunchedEffect(selected, composition) {
        if (selected && composition != null) {
            animatable.animate(composition, iterations = 1)
        }
    }

    val tint = LocalContentColor.current.toArgb()
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(property = LottieProperty.COLOR, value = tint, "**"),
        rememberLottieDynamicProperty(property = LottieProperty.STROKE_COLOR, value = tint, "**")
    )

    LottieAnimation(
        composition = composition,
        progress = { animatable.progress },
        dynamicProperties = dynamicProperties,
        modifier = Modifier
            .size(24.dp)
            .semantics { this.contentDescription = contentDescription }
    )
}

// Quill's approach: the AVD runs forward when the tab becomes selected.
@Composable
private fun AnimatedNavIcon(
    @DrawableRes avdRes: Int,
    selected: Boolean,
    contentDescription: String
) {
    val image = AnimatedImageVector.animatedVectorResource(avdRes)
    Icon(
        painter = rememberAnimatedVectorPainter(
            animatedImageVector = image,
            atEnd = selected
        ),
        contentDescription = contentDescription
    )
}
