package com.yugentech.ryori.ui.main.mainScreen.components

import android.os.SystemClock
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.delay

// Same setup as Quill's BottomBar: default Material NavigationBar colors and label styling,
// and re-tapping the current tab does nothing. Home and Search use Lordicon Lottie icons that
// play their "pinch" once when the tab is selected; Settings uses Sessions' animated gear.
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

                        // Sessions' settings gear, played once on selection like Home and Search.
                        BottomBarScreen.More -> LottieNavIcon(
                            rawRes = R.raw.lottie_nav_settings,
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

    // As in Sessions' nav bar: selecting the tab starts a play, and nothing but a new selection
    // restarts it. Tapping another tab straight away doesn't cancel it, so it always finishes on
    // its resting pose instead of freezing mid-way.
    var plays by remember { mutableIntStateOf(0) }
    LaunchedEffect(selected) {
        if (selected) plays++
    }
    LaunchedEffect(plays, composition) {
        val loaded = composition
        if (plays > 0 && loaded != null) {
            animatable.animate(loaded, iterations = 1)
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

// The AVD runs forward when the tab becomes selected and back when it's left. Each run gets its
// full length before the next starts (as Sessions lets its icons finish): tapping away straight
// after selecting lets the forward animation complete, then it plays back to rest.
@Composable
private fun AnimatedNavIcon(
    @DrawableRes avdRes: Int,
    selected: Boolean,
    contentDescription: String
) {
    val image = AnimatedImageVector.animatedVectorResource(avdRes)
    var atEnd by remember { mutableStateOf(selected) }
    var lastRunStart by remember { mutableLongStateOf(0L) }

    LaunchedEffect(selected) {
        if (atEnd == selected) return@LaunchedEffect
        val remaining = image.totalDuration - (SystemClock.uptimeMillis() - lastRunStart)
        if (remaining > 0) delay(remaining)
        atEnd = selected
        lastRunStart = SystemClock.uptimeMillis()
    }

    Icon(
        painter = rememberAnimatedVectorPainter(
            animatedImageVector = image,
            atEnd = atEnd
        ),
        contentDescription = contentDescription
    )
}
