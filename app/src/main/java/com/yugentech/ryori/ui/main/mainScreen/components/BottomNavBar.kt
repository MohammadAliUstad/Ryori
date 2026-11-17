package com.yugentech.ryori.ui.main.mainScreen.components

import androidx.compose.ui.res.stringResource
import android.os.SystemClock
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
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

/** Height of the dock. */
private val DockHeight: Dp = 64.dp

/** Height of each tab's capsule inside the dock. */
private val TabHeight = 48.dp

// Sessions' floating pill dock: each tab is just its animated icon, and the selected one grows
// into a filled capsule that also spells out its name. Re-tapping the current tab does nothing.
@Composable
fun BottomNavBar(
    currentTab: BottomBarScreen,
    onTabSelected: (BottomBarScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.height(DockHeight),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                BottomBarScreen.all.forEach { screen ->
                    DockTab(
                        screen = screen,
                        isSelected = screen == currentTab,
                        onClick = { if (screen != currentTab) onTabSelected(screen) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DockTab(
    screen: BottomBarScreen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val motion = MaterialTheme.motionScheme
    val container by animateColorAsState(
        targetValue = if (isSelected) colors.primaryContainer else Color.Transparent,
        animationSpec = motion.fastEffectsSpec(),
        label = "dockTabContainer"
    )
    val content by animateColorAsState(
        targetValue = if (isSelected) colors.onPrimaryContainer else colors.onSurfaceVariant,
        animationSpec = motion.fastEffectsSpec(),
        label = "dockTabContent"
    )
    val horizontalPad by animateDpAsState(
        targetValue = if (isSelected) 16.dp else 12.dp,
        animationSpec = motion.fastSpatialSpec(),
        label = "dockTabPad"
    )
    val title = stringResource(screen.title)
    val animationRes = when (screen) {
        BottomBarScreen.Home -> R.raw.lottie_nav_home
        BottomBarScreen.Search -> R.raw.lottie_nav_search
        BottomBarScreen.More -> R.raw.lottie_nav_settings
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(TabHeight)
            .clip(CircleShape)
            .background(container)
            // No ripple and no press scale: the capsule growing is the feedback
            .selectable(
                selected = isSelected,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .semantics { contentDescription = title }
            .padding(horizontal = horizontalPad)
    ) {
        CompositionLocalProvider(LocalContentColor provides content) {
            LottieNavIcon(
                rawRes = animationRes,
                selected = isSelected,
                contentDescription = title
            )
        }
        // The name slides out beside the icon on the selected tab only
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn(motion.fastEffectsSpec()) +
                expandHorizontally(motion.fastSpatialSpec(), expandFrom = Alignment.Start),
            exit = fadeOut(motion.fastEffectsSpec()) +
                shrinkHorizontally(motion.fastSpatialSpec(), shrinkTowards = Alignment.Start)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = content,
                maxLines = 1,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

// Lordicon exports are drawn in one dark colour; every fill and stroke is re-tinted with the
// dock tab's content colour, so it follows the theme and the selected state.
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
