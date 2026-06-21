package com.yugentech.ryori.ui.config.aboutScreen.components

import android.graphics.drawable.Animatable
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.yugentech.ryori.R

// Hosts avd_ryori_burger in an ImageView (Compose can't drive a multi-target AVD with keyframed
// sets directly), same approach as Quill's AnimatedQuillIcon. The drawable rests on the fully
// assembled burger; flipping isAnimating to true replays the build-up from the start.
@Composable
fun AnimatedRyoriIcon(
    isAnimating: Boolean,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            ImageView(ctx).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageResource(R.drawable.avd_ryori_burger)
            }
        },
        update = { imageView ->
            if (isAnimating) {
                (imageView.drawable as? Animatable)?.let { animatable ->
                    animatable.stop()
                    animatable.start()
                }
            }
        }
    )
}
