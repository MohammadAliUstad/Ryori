package com.yugentech.ryori.ui.main.mainScreen.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos

// Ported from Quill's Discover screen (ui/tabs/discoverScreen/components/LoadingPulse.kt).

/** One full breath of a placeholder: from its resting tone to its brightest and back. */
private const val PULSE_MS = 1600L

/** How much later each placeholder breathes per screen width across, so a row breathes as a wave. */
private const val WAVE_DELAY_MS = 480L

/**
 * The standard loading placeholder: no moving highlight, just a calm pulse. Each placeholder
 * breathes between two surface tones (lifting toward a hint of the theme's primary at its
 * brightest), eased in and out. Placeholders further across the screen breathe a little later,
 * so a row of cards pulses as one soft wave from left to right. Every placeholder runs off the
 * same frame clock, so they stay in step. Clip before it: it fills whatever bounds it's given.
 */
fun Modifier.loadingPulse(): Modifier = composed {
    val scheme = MaterialTheme.colorScheme
    val isDark = scheme.surface.luminance() < 0.5f
    val resting = scheme.surfaceContainerHigh
    val bright = lerp(scheme.surfaceContainerHighest, scheme.primary, if (isDark) 0.08f else 0.05f)
    val screenWidth = with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }

    var originX by remember { mutableFloatStateOf(0f) }
    var frameTimeMs by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) withFrameMillis { frameTimeMs = it }
    }

    this
        .onGloballyPositioned { originX = it.positionInRoot().x }
        .drawBehind {
            // Where this placeholder sits across the screen sets how far behind the wave it runs
            val delay = (originX / screenWidth).coerceIn(0f, 1.5f) * WAVE_DELAY_MS
            val phase = ((frameTimeMs - delay) % PULSE_MS + PULSE_MS) % PULSE_MS / PULSE_MS
            // 0 → 1 → 0, eased at both ends
            val breath = (1f - cos(2f * PI.toFloat() * phase)) / 2f
            drawRect(lerp(resting, bright, breath))
        }
}
