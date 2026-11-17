package com.yugentech.ryori.ui.onboardingScreen.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BakeryDining
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DinnerDining
import androidx.compose.material.icons.rounded.EggAlt
import androidx.compose.material.icons.rounded.Grass
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.LocalBar
import androidx.compose.material.icons.rounded.LocalPizza
import androidx.compose.material.icons.rounded.LunchDining
import androidx.compose.material.icons.rounded.Opacity
import androidx.compose.material.icons.rounded.RamenDining
import androidx.compose.material.icons.rounded.RiceBowl
import androidx.compose.material.icons.rounded.SetMeal
import androidx.compose.material.icons.rounded.SoupKitchen
import androidx.compose.material.icons.rounded.Swipe
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.yugentech.ryori.R
import com.yugentech.ryori.api.model.domain.CuisineFlags
import com.yugentech.ryori.ui.main.mainScreen.components.pressClickable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sin

// ---------------------------------------------------------------- Shared pieces

/**
 * A 0 → 1 entrance that starts [delayMs] after the scene becomes [active], on a soft spring
 * (it overshoots a touch, so things settle rather than stop).
 */
@Composable
private fun rememberEntrance(active: Boolean, delayMs: Int): Animatable<Float, *> {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(active) {
        if (active) {
            delay(delayMs.toLong())
            progress.animateTo(1f, spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow))
        }
    }
    return progress
}

/** Fades in and rises [rise] into place as [progress] runs 0 → 1. */
private fun Modifier.rise(progress: () -> Float, rise: Dp = 24.dp): Modifier = graphicsLayer {
    val t = progress()
    alpha = t.coerceIn(0f, 1f)
    translationY = (1f - t) * rise.toPx()
}

/** A scene's headline and the line under it, rising in after [delayMs]. */
@Composable
private fun SceneText(title: String, body: String, active: Boolean, delayMs: Int) {
    val titleIn = rememberEntrance(active, delayMs)
    val bodyIn = rememberEntrance(active, delayMs + 140)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.rise({ titleIn.value })
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.82f),
            modifier = Modifier
                .widthIn(max = 420.dp)
                .rise({ bodyIn.value })
        )
    }
}

/** A hint with an icon: plain primary text straight on the aura, no container. */
@Composable
private fun Hint(text: String, icon: ImageVector, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = primary)
        Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = primary)
    }
}

/** Every scene's frame: centred, with room at the sides. */
@Composable
private fun SceneColumn(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) { content() }
}

/** One of the paper theme's three container tones, with the colour that reads on it. */
@Composable
private fun tone(index: Int): Pair<Color, Color> {
    val colors = paperColors()
    return when (index % 3) {
        0 -> colors.primaryContainer to colors.onPrimaryContainer
        1 -> colors.secondaryContainer to colors.onSecondaryContainer
        else -> colors.tertiaryContainer to colors.onTertiaryContainer
    }
}

// ---------------------------------------------------------------- 1. Discover: a deck of dishes

private class Dish(val name: String, @param:StringRes val cuisine: Int, val area: String, val icon: ImageVector)

private val Dishes = listOf(
    Dish("Shoyu Ramen", R.string.cuisine_japanese, "Japanese", Icons.Rounded.RamenDining),
    Dish("Margherita", R.string.cuisine_italian, "Italian", Icons.Rounded.LocalPizza),
    Dish("Butter Chicken", R.string.cuisine_indian, "Indian", Icons.Rounded.SoupKitchen),
    Dish("Tacos al Pastor", R.string.cuisine_mexican, "Mexican", Icons.Rounded.LunchDining),
    Dish("Pad Thai", R.string.cuisine_thai, "Thai", Icons.Rounded.RiceBowl),
    Dish("Shakshuka", R.string.cuisine_tunisian, "Tunisian", Icons.Rounded.EggAlt),
    Dish("Bourguignon", R.string.cuisine_french, "French", Icons.Rounded.DinnerDining),
    Dish("Moussaka", R.string.cuisine_greek, "Greek", Icons.Rounded.BakeryDining)
)

/** How many cards of the deck show at once: the top one and two waiting behind it. */
private const val DECK_DEPTH = 3

/** How far the deck's cards and shadows can reach past its 280 x 316 box (the back card sits
 * lower and tilted), so its fading frame can hold all of it. */
private val DeckOverhang = 60.dp

/**
 * A deck of dish cards. Drag the top card sideways and it tilts with the finger; let go past
 * the threshold and it's thrown off, the next card rising up into its place (a tap throws it
 * too). Shortly after the scene opens, the top card leans out and back once, so the deck
 * reads as something to swipe.
 */
@Composable
internal fun DiscoverScene(active: Boolean) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val deckIn = rememberEntrance(active, 150)
    val hintIn = rememberEntrance(active, 1300)
    val flingAway = with(density) { 440.dp.toPx() }
    val threshold = with(density) { 90.dp.toPx() }
    // Counts up forever: each card keeps its identity as it moves from the back to the top
    var topCard by remember { mutableIntStateOf(0) }
    val drag = remember { Animatable(0f) }
    var touched by remember { mutableStateOf(false) }

    LaunchedEffect(active) {
        if (!active) return@LaunchedEffect
        delay(1500)
        if (!touched) {
            drag.animateTo(threshold * 0.5f, tween(360, easing = FastOutSlowInEasing))
            drag.animateTo(0f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessLow))
        }
    }

    fun throwCard(direction: Float) {
        scope.launch {
            haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
            drag.animateTo(direction * flingAway, tween(240, easing = FastOutLinearInEasing))
            topCard++
            drag.snapTo(0f)
        }
    }

    SceneColumn {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(width = 280.dp, height = 316.dp)
        ) {
          // The deck fades in on a layer, and a fading layer is drawn off screen clipped to its own
          // bounds: the back card, lower and tilted with its shadow, hangs past the 280 x 316 box
          // and would be cut off until the fade ends. This frame is bigger on every side (centred,
          // so nothing moves) and holds the whole deck, overhang included.
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .requiredSize(width = 280.dp + DeckOverhang * 2, height = 316.dp + DeckOverhang * 2)
                .rise({ deckIn.value }, 40.dp)
          ) {
            // Back to front, so the top card draws last
            for (depth in DECK_DEPTH - 1 downTo 0) {
                val card = topCard + depth
                key(card) {
                    val dish = Dishes[card % Dishes.size]
                    // Each card leans its own way, kept as it moves up the deck
                    val lean = if (card % 2 == 0) 7f else -7f
                    // A card joining the back of the deck after a throw fades in and settles up
                    // from one step further back; the opening cards are already in place
                    val appear = remember { Animatable(if (card < DECK_DEPTH) 1f else 0f) }
                    LaunchedEffect(Unit) {
                        appear.animateTo(1f, tween(380, easing = FastOutSlowInEasing))
                    }
                    val cardModifier = Modifier
                        .zIndex((DECK_DEPTH - depth).toFloat())
                        .graphicsLayer {
                            if (depth == 0) {
                                translationX = drag.value
                                rotationZ = drag.value / flingAway * 18f
                            } else {
                                // The cards behind move up a step as the top one is pulled away
                                val pull = (abs(drag.value) / threshold).coerceIn(0f, 1f)
                                val step = depth - pull + (1f - appear.value)
                                alpha = appear.value
                                // Fade the drawing itself rather than an off-screen copy clipped
                                // to the card, which would cut its shadow off while it fades in
                                compositingStrategy = CompositingStrategy.ModulateAlpha
                                // Far enough out that the waiting cards' edges show around the top one
                                val scale = 1f - step * 0.05f
                                scaleX = scale
                                scaleY = scale
                                translationY = step * 22.dp.toPx()
                                translationX = lean * step * 2.dp.toPx()
                                rotationZ = lean * step
                            }
                        }
                    DishCard(
                        dish = dish,
                        tone = card,
                        modifier = if (depth != 0) cardModifier else cardModifier
                            .pointerInput(card) {
                                detectHorizontalDragGestures(
                                    onDragStart = { touched = true },
                                    onDragEnd = {
                                        if (abs(drag.value) > threshold) {
                                            throwCard(sign(drag.value))
                                        } else {
                                            scope.launch {
                                                drag.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow))
                                            }
                                        }
                                    },
                                    onDragCancel = { scope.launch { drag.animateTo(0f, spring()) } },
                                    onHorizontalDrag = { change, dx ->
                                        // Consumed, so the pager under the card doesn't page
                                        change.consume()
                                        scope.launch { drag.snapTo(drag.value + dx) }
                                    }
                                )
                            }
                            .clickable(role = Role.Button) {
                                touched = true
                                throwCard(1f)
                            }
                    )
                }
            }
          }
        }
        Spacer(Modifier.height(24.dp))
        Hint(
            text = stringResource(R.string.onboarding_discover_swipe),
            icon = Icons.Rounded.Swipe,
            modifier = Modifier.rise({ hintIn.value }, 12.dp)
        )
        Spacer(Modifier.height(28.dp))
        SceneText(
            title = stringResource(R.string.onboarding_discover_title),
            body = stringResource(R.string.onboarding_discover_body),
            active = active,
            delayMs = 800
        )
    }
}

/** One dish: a tall card in a soft gradient of one container tone, its flag, art and name. */
@Composable
private fun DishCard(dish: Dish, tone: Int, modifier: Modifier = Modifier) {
    val colors = paperColors()
    val (container, content) = tone(tone)
    val shape = RoundedCornerShape(32.dp)
    Box(
        modifier = modifier
            .size(width = 240.dp, height = 284.dp)
            .shadow(10.dp, shape, ambientColor = Color.Black.copy(alpha = 0.25f), spotColor = Color.Black.copy(alpha = 0.25f))
            .clip(shape)
            .background(Brush.verticalGradient(listOf(container, lerp(container, colors.surface, 0.45f))))
    ) {
        Text(
            text = CuisineFlags.flag(dish.area),
            fontSize = 26.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(20.dp)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-22).dp)
                .size(124.dp)
                .clip(CircleShape)
                .background(content.copy(alpha = 0.08f))
        ) {
            Icon(dish.icon, contentDescription = null, tint = content, modifier = Modifier.size(68.dp))
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(22.dp)
        ) {
            Text(
                text = stringResource(dish.cuisine),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = content.copy(alpha = 0.7f)
            )
            Text(
                text = dish.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ---------------------------------------------------------------- 2. Cook along: into the pot

private class Ingredient(@param:StringRes val name: Int, val icon: ImageVector)

private val PotIngredients = listOf(
    Ingredient(R.string.onb_ing_noodles, Icons.Rounded.RamenDining),
    Ingredient(R.string.onb_ing_stock, Icons.Rounded.WaterDrop),
    Ingredient(R.string.onb_ing_soy, Icons.Rounded.Opacity),
    Ingredient(R.string.onb_ing_eggs, Icons.Rounded.EggAlt),
    Ingredient(R.string.onb_ing_onions, Icons.Rounded.Grass),
    Ingredient(R.string.onb_ing_nori, Icons.Rounded.Layers)
)

/**
 * A glass pot over the ingredients for a bowl of ramen. Tap an ingredient and it goes in: the
 * broth rises with a splash and the steam grows. Left alone, the ingredients go in by
 * themselves; once the pot is full a check pops up, then it empties for another go.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun IngredientsScene(active: Boolean) {
    val haptic = LocalHapticFeedback.current
    val potIn = rememberEntrance(active, 150)
    val hintIn = rememberEntrance(active, 1500)
    val added = remember { mutableStateListOf<Int>() }
    var touched by remember { mutableStateOf(false) }
    val level = remember { Animatable(0f) }
    val splash = remember { Animatable(1f) }
    val total = PotIngredients.size
    val full = added.size >= total

    fun add(index: Int) {
        if (index in added) return
        added.add(index)
        haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
    }

    LaunchedEffect(added.size) {
        launch { level.animateTo(added.size / total.toFloat(), spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow)) }
        if (added.isNotEmpty()) {
            splash.snapTo(0f)
            splash.animateTo(1f, tween(650, easing = FastOutSlowInEasing))
        }
    }

    LaunchedEffect(active, added.size, touched) {
        if (!active) return@LaunchedEffect
        if (full) {
            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
            delay(2600)
            added.clear()
            return@LaunchedEffect
        }
        if (touched) return@LaunchedEffect
        delay(if (added.isEmpty()) 2000 else 1300)
        add(PotIngredients.indices.first { it !in added })
    }

    val infinite = rememberInfiniteTransition(label = "pot")
    val wave by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing)),
        label = "potWave"
    )
    val steam by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing)),
        label = "potSteam"
    )
    val checkScale by animateFloatAsState(
        if (full) 1f else 0f,
        spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMediumLow),
        label = "potCheck"
    )

    val ink = MaterialTheme.colorScheme.onBackground
    val broth = MaterialTheme.colorScheme.primary
    val onBroth = MaterialTheme.colorScheme.onPrimary
    // The handles and the lid are solid: drawn in the full ink colour, no transparency at all
    val handle = ink
    val lid = ink

    SceneColumn {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(width = 230.dp, height = 168.dp)
                .rise({ potIn.value }, 36.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val potTop = h * 0.40f
                val left = w * 0.18f
                val right = w * 0.82f
                val bottom = h * 0.97f
                val corner = 30.dp.toPx()
                val body = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = left, top = potTop, right = right, bottom = bottom,
                            topLeftCornerRadius = CornerRadius(4.dp.toPx()),
                            topRightCornerRadius = CornerRadius(4.dp.toPx()),
                            bottomRightCornerRadius = CornerRadius(corner),
                            bottomLeftCornerRadius = CornerRadius(corner)
                        )
                    )
                }

                // Steam: three wisps drifting up, stronger the fuller the pot
                val strength = level.value.coerceIn(0f, 1f)
                if (strength > 0f) {
                    for (i in 0..2) {
                        val t = (steam + i / 3f) % 1f
                        val x = left + (right - left) * (0.3f + 0.2f * i)
                        val y = potTop - 10.dp.toPx() - t * (potTop - 14.dp.toPx())
                        val wisp = Path().apply {
                            moveTo(x, y + 14.dp.toPx())
                            cubicTo(
                                x - 7.dp.toPx(), y + 8.dp.toPx(),
                                x + 7.dp.toPx(), y + 4.dp.toPx(),
                                x, y - 4.dp.toPx()
                            )
                        }
                        drawPath(
                            wisp,
                            color = ink.copy(alpha = 0.35f * strength * sin(PI.toFloat() * t)),
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                // The glass and the broth inside it
                drawPath(body, ink.copy(alpha = 0.07f))
                val surface = bottom - 6.dp.toPx() - level.value * (bottom - potTop - 20.dp.toPx())
                if (level.value > 0.001f) {
                    clipPath(body) {
                        val amplitude = 3.dp.toPx()
                        val liquid = Path().apply {
                            moveTo(left, surface)
                            val steps = 24
                            for (s in 0..steps) {
                                val x = left + (right - left) * s / steps
                                val y = surface + sin(wave + s / steps.toFloat() * 2f * PI.toFloat()) * amplitude
                                lineTo(x, y)
                            }
                            lineTo(right, bottom)
                            lineTo(left, bottom)
                            close()
                        }
                        drawPath(liquid, Brush.verticalGradient(listOf(broth.copy(alpha = 0.75f), broth), startY = surface, endY = bottom))
                    }
                }
                drawPath(body, ink.copy(alpha = 0.35f), style = Stroke(width = 2.dp.toPx()))
                // The handles, after the glass and the broth: each reaches a little into the pot,
                // and drawn first the rising broth and the glass painted over their inner ends
                drawRoundRect(
                    handle,
                    topLeft = Offset(left - 20.dp.toPx(), potTop + 14.dp.toPx()),
                    size = Size(24.dp.toPx(), 10.dp.toPx()),
                    cornerRadius = CornerRadius(5.dp.toPx())
                )
                drawRoundRect(
                    handle,
                    topLeft = Offset(right - 4.dp.toPx(), potTop + 14.dp.toPx()),
                    size = Size(24.dp.toPx(), 10.dp.toPx()),
                    cornerRadius = CornerRadius(5.dp.toPx())
                )
                // The rim
                drawRoundRect(
                    lid,
                    topLeft = Offset(left - 8.dp.toPx(), potTop - 6.dp.toPx()),
                    size = Size(right - left + 16.dp.toPx(), 10.dp.toPx()),
                    cornerRadius = CornerRadius(5.dp.toPx())
                )

                // A splash as something goes in: two drops jump up from the surface and fall back
                val s = splash.value
                if (s < 1f) {
                    val jump = sin(PI.toFloat() * s) * 22.dp.toPx()
                    val spread = 10.dp.toPx() + s * 14.dp.toPx()
                    val cx = (left + right) / 2f
                    for (dir in listOf(-1f, 1f)) {
                        drawCircle(
                            broth.copy(alpha = 1f - s),
                            radius = 4.dp.toPx(),
                            center = Offset(cx + dir * spread, surface - 6.dp.toPx() - jump)
                        )
                    }
                }
            }
            // Full: a check pops up out of the broth
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset(y = 34.dp)
                    .graphicsLayer {
                        scaleX = checkScale
                        scaleY = checkScale
                        alpha = checkScale.coerceIn(0f, 1f)
                    }
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(onBroth)
            ) {
                Icon(Icons.Rounded.Check, contentDescription = null, tint = broth, modifier = Modifier.size(26.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.rise({ potIn.value }, 20.dp)
        ) {
            AnimatedContent(
                targetState = full,
                transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(120)) },
                label = "potPhase"
            ) { done ->
                Text(
                    text = stringResource(if (done) R.string.onboarding_ready_to_cook else R.string.onboarding_gathering),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = ink.copy(alpha = 0.8f)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "${added.size}/$total",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = broth
            )
        }
        Spacer(Modifier.height(16.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PotIngredients.forEachIndexed { i, ingredient ->
                IngredientChip(
                    ingredient = ingredient,
                    added = i in added,
                    entrance = rememberEntrance(active, 400 + i * 70),
                    onClick = {
                        touched = true
                        add(i)
                    }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Hint(
            text = stringResource(R.string.onboarding_cook_tap),
            icon = Icons.Rounded.TouchApp,
            modifier = Modifier.rise({ hintIn.value }, 12.dp)
        )
        Spacer(Modifier.height(20.dp))
        SceneText(
            title = stringResource(R.string.onboarding_cook_title),
            body = stringResource(R.string.onboarding_cook_body),
            active = active,
            delayMs = 800
        )
    }
}

/** An ingredient waiting to go in: glassy until it's added, then lit in the accent with a check. */
@Composable
private fun IngredientChip(
    ingredient: Ingredient,
    added: Boolean,
    entrance: Animatable<Float, *>,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val container by animateColorAsState(
        if (added) colors.primary.copy(alpha = 0.16f) else colors.onBackground.copy(alpha = 0.06f),
        tween(300),
        label = "chipContainer"
    )
    val content by animateColorAsState(
        if (added) colors.primary else colors.onBackground.copy(alpha = 0.85f),
        tween(300),
        label = "chipContent"
    )
    val outline by animateColorAsState(
        if (added) colors.primary.copy(alpha = 0.4f) else colors.onBackground.copy(alpha = 0.10f),
        tween(300),
        label = "chipOutline"
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .rise({ entrance.value }, 14.dp)
            .pressClickable(shape = CircleShape, enabled = !added, onClick = onClick)
            .clip(CircleShape)
            .background(container)
            .border(1.dp, outline, CircleShape)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Icon(
            imageVector = if (added) Icons.Rounded.Check else ingredient.icon,
            contentDescription = null,
            tint = content,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = stringResource(ingredient.name),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = content,
            maxLines = 1
        )
    }
}

// ---------------------------------------------------------------- 3. Explore: around the world

private class Stop(@param:StringRes val name: Int, val area: String)

private val Stops = listOf(
    Stop(R.string.cuisine_japanese, "Japanese"),
    Stop(R.string.cuisine_italian, "Italian"),
    Stop(R.string.cuisine_mexican, "Mexican"),
    Stop(R.string.cuisine_indian, "Indian"),
    Stop(R.string.cuisine_french, "French"),
    Stop(R.string.cuisine_thai, "Thai"),
    Stop(R.string.cuisine_greek, "Greek"),
    Stop(R.string.cuisine_tunisian, "Tunisian")
)

/** How far the flags orbit from the centre. */
private val OrbitRadius = 118.dp

/**
 * Cuisines circling the globe: flags drift slowly round a dotted orbit and the one being visited
 * swells, its name in the bubble at the centre. It hops from stop to stop on its own; the flags
 * are just to look at.
 */
@Composable
internal fun ExploreScene(active: Boolean) {
    val density = LocalDensity.current
    val orbitIn = rememberEntrance(active, 150)
    var picked by remember { mutableIntStateOf(0) }
    val radius = with(density) { OrbitRadius.toPx() }

    val spin by rememberInfiniteTransition(label = "orbit").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(60_000, easing = LinearEasing)),
        label = "orbitAngle"
    )

    LaunchedEffect(active) {
        if (!active) return@LaunchedEffect
        while (true) {
            delay(2000)
            picked = (picked + 1) % Stops.size
        }
    }

    val colors = MaterialTheme.colorScheme
    SceneColumn {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(300.dp)
                .graphicsLayer {
                    // The whole globe grows into place rather than rising
                    val t = orbitIn.value
                    alpha = t.coerceIn(0f, 1f)
                    scaleX = 0.8f + 0.2f * t
                    scaleY = 0.8f + 0.2f * t
                }
        ) {
            val ring = colors.onBackground.copy(alpha = 0.16f)
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    ring,
                    radius = radius,
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.dp.toPx(), 7.dp.toPx()))
                    )
                )
                drawCircle(ring.copy(alpha = 0.08f), radius = radius * 0.62f)
            }
            // The cuisine being visited
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(136.dp)
                    .clip(CircleShape)
                    .background(colors.onBackground.copy(alpha = 0.06f))
                    .border(1.dp, colors.onBackground.copy(alpha = 0.10f), CircleShape)
            ) {
                AnimatedContent(
                    targetState = picked,
                    transitionSpec = {
                        (fadeIn(tween(260)) + scaleIn(tween(260), initialScale = 0.7f))
                            .togetherWith(fadeOut(tween(140)) + scaleOut(tween(140), targetScale = 1.15f))
                    },
                    contentAlignment = Alignment.Center,
                    label = "orbitPick"
                ) { i ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(CuisineFlags.flag(Stops[i].area), fontSize = 40.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(Stops[i].name),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onBackground,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
            Stops.forEachIndexed { i, stop ->
                val selected = i == picked
                val scale by animateFloatAsState(
                    if (selected) 1.2f else 1f,
                    spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow),
                    label = "flagScale"
                )
                val outline by animateColorAsState(
                    if (selected) colors.primary else colors.onBackground.copy(alpha = 0.10f),
                    tween(250),
                    label = "flagOutline"
                )
                val fill by animateColorAsState(
                    if (selected) colors.primary.copy(alpha = 0.18f) else colors.onBackground.copy(alpha = 0.06f),
                    tween(250),
                    label = "flagFill"
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        // Placed from the spin at layout time, so the orbit never recomposes
                        .offset {
                            val angle = Math.toRadians((spin + i * 360f / Stops.size - 90f).toDouble())
                            IntOffset((cos(angle) * radius).roundToInt(), (sin(angle) * radius).roundToInt())
                        }
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(fill)
                        .border(if (selected) 2.dp else 1.dp, outline, CircleShape)
                ) {
                    Text(CuisineFlags.flag(stop.area), fontSize = 24.sp)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        SceneText(
            title = stringResource(R.string.onboarding_explore_title),
            body = stringResource(R.string.onboarding_explore_body),
            active = active,
            delayMs = 700
        )
    }
}

// ---------------------------------------------------------------- 4. Your kitchen: snapshots

private class Snapshot(val name: String, @param:StringRes val cuisine: Int, val icon: ImageVector)

private val Snapshots = listOf(
    Snapshot("Shoyu Ramen", R.string.cuisine_japanese, Icons.Rounded.RamenDining),
    Snapshot("Butter Chicken", R.string.cuisine_indian, Icons.Rounded.SoupKitchen),
    Snapshot("Grilled Salmon", R.string.cuisine_norwegian, Icons.Rounded.SetMeal),
    Snapshot("Mojito", R.string.onb_cocktail, Icons.Rounded.LocalBar)
)

// Where each snapshot lands in the fan
private val FanX = listOf(-84, -28, 28, 84)
private val FanY = listOf(20, 4, 4, 20)
private val FanTilt = listOf(-14f, -5f, 5f, 14f)

private const val DEMO_VIEWED = 36
private const val DEMO_TICKED = 128

/**
 * The recipes you've opened, dealt out like snapshots into a fan. Tap one and it lifts out of
 * the fan; tap again and it drops back. Underneath, the kitchen's stats count up.
 */
@Composable
internal fun KitchenScene(active: Boolean) {
    val haptic = LocalHapticFeedback.current
    val deal = List(Snapshots.size) { rememberEntrance(active, 250 + it * 160) }
    val statsIn = rememberEntrance(active, 1000)
    var lifted by remember { mutableIntStateOf(-1) }
    val count = remember { Animatable(0f) }

    LaunchedEffect(active) {
        if (!active) return@LaunchedEffect
        delay(1100)
        count.animateTo(1f, tween(1400, easing = FastOutSlowInEasing))
        haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
    }

    SceneColumn {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            Snapshots.forEachIndexed { i, snap ->
                val lift by animateFloatAsState(
                    if (lifted == i) 1f else 0f,
                    spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
                    label = "snapLift"
                )
                Polaroid(
                    snapshot = snap,
                    tone = i,
                    modifier = Modifier
                        .zIndex(if (lifted == i) 10f else i.toFloat())
                        .graphicsLayer {
                            val p = deal[i].value
                            alpha = p.coerceIn(0f, 1f)
                            translationX = FanX[i].dp.toPx() * p
                            translationY = (1f - p) * 200.dp.toPx() + FanY[i].dp.toPx() - lift * 24.dp.toPx()
                            rotationZ = FanTilt[i] * p * (1f - lift * 0.7f)
                            val scale = 1f + lift * 0.1f
                            scaleX = scale
                            scaleY = scale
                        }
                        .pressClickable {
                            haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                            lifted = if (lifted == i) -1 else i
                        }
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .rise({ statsIn.value }, 16.dp)
        ) {
            StatPill(
                value = "${(DEMO_VIEWED * count.value).roundToInt()}",
                label = stringResource(R.string.chef_stat_viewed),
                modifier = Modifier.weight(1f)
            )
            StatPill(
                value = "${(DEMO_TICKED * count.value).roundToInt()}",
                label = stringResource(R.string.chef_stat_ticked),
                modifier = Modifier.weight(1f)
            )
            StatPill(
                value = CuisineFlags.flag("Japanese"),
                label = stringResource(R.string.chef_stat_cuisine),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(28.dp))
        SceneText(
            title = stringResource(R.string.onboarding_kitchen_title),
            body = stringResource(R.string.onboarding_kitchen_body),
            active = active,
            delayMs = 600
        )
    }
}

/** A recipe as an instant photo: a white border, the dish in a container tone, its name below. */
@Composable
private fun Polaroid(snapshot: Snapshot, tone: Int, modifier: Modifier = Modifier) {
    val colors = paperColors()
    val (container, content) = tone(tone)
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = modifier
            .width(122.dp)
            .shadow(8.dp, shape, ambientColor = Color.Black.copy(alpha = 0.3f), spotColor = Color.Black.copy(alpha = 0.3f))
            .clip(shape)
            .background(colors.surfaceContainerLowest)
            .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Brush.linearGradient(listOf(container, lerp(container, colors.surface, 0.4f))))
        ) {
            Icon(snapshot.icon, contentDescription = null, tint = content, modifier = Modifier.size(44.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = snapshot.name,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = colors.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = stringResource(snapshot.cuisine),
            style = MaterialTheme.typography.labelSmall,
            color = colors.onSurfaceVariant,
            maxLines = 1
        )
    }
}

/** One kitchen stat: a big value over its name, on a glassy wash. */
@Composable
private fun StatPill(value: String, label: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(20.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(shape)
            .background(colors.onBackground.copy(alpha = 0.06f))
            .border(1.dp, colors.onBackground.copy(alpha = 0.10f), shape)
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = colors.primary
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.onBackground.copy(alpha = 0.75f),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}
