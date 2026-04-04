package com.stuf.itinder.main.feed

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalDragOrCancellation
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.stuf.itinder.R
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs
import kotlin.math.hypot

/** Radial gradient radius as a multiple of screen diagonal (smaller = tighter corner glow). */
private const val SwipeRadialRadiusKoef = 0.92f

/** Center sits past the side edge by this fraction of width (dislike: left, like: right). */
private const val SwipeRadialCenterXKoef = 0.22f

/** Extra distance below the bottom edge, as a fraction of height (keeps center near the corner, not far under the screen). */
private const val SwipeRadialCenterYKoef = 0.14f
private const val DismissThresholdFraction = 0.22f
private const val CardEnterInitialScale = 0.92f

private sealed interface SwipeOffsetCommand {
    data class DragDelta(val dx: Float) : SwipeOffsetCommand
    data object PointerUp : SwipeOffsetCommand
    data class ProgrammaticDismiss(val liked: Boolean) : SwipeOffsetCommand
}

@Composable
fun SwipeableFeedCardHost(
    profile: FeedUserProfile,
    onConsumed: (liked: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = remember(configuration, density) {
        with(density) { configuration.screenWidthDp.dp.toPx() }
    }
    val screenHeightPx = remember(configuration, density) {
        with(density) { configuration.screenHeightDp.dp.toPx() }
    }
    val haptic = LocalHapticFeedback.current

    var containerWidthPx by remember { mutableFloatStateOf(0f) }
    val containerWidthUpdated by rememberUpdatedState(containerWidthPx)
    val onConsumedUpdated by rememberUpdatedState(onConsumed)
    val screenWidthUpdated by rememberUpdatedState(screenWidthPx)

    var cardHostLeftInRootPx by remember(profile.id) { mutableFloatStateOf(0f) }
    var cardHostTopInRootPx by remember(profile.id) { mutableFloatStateOf(0f) }
    val swipeCommands = remember(profile.id) { Channel<SwipeOffsetCommand>(Channel.UNLIMITED) }

    /** True while exit animation runs (finger over threshold or Pass/Like). */
    val cardDismissInFlight = remember(profile.id) { AtomicBoolean(false) }

    /** Ensures onConsumed runs at most once per profile (e.g. two queued ProgrammaticDismiss). */
    val cardConsumed = remember(profile.id) { AtomicBoolean(false) }

    /** False until enter scale 0.92→1 finishes; blocks taps/drags that would queue dismiss mid-enter. */
    val cardEnterComplete = remember(profile.id) { AtomicBoolean(false) }

    val offsetX = remember(profile.id) { Animatable(0f) }
    val cardScale = remember(profile.id) { Animatable(CardEnterInitialScale) }

    val dislikeColor = colorResource(R.color.action_btn_dislike)
    val likeColor = colorResource(R.color.action_btn_like)
    val bigGap = dimensionResource(R.dimen.big_gap)

    fun exitTargetPx(liked: Boolean): Float {
        val w = maxOf(containerWidthUpdated, screenWidthUpdated * 0.5f).coerceAtLeast(1f)
        return if (liked) w + screenWidthUpdated * 0.55f else -(w + screenWidthUpdated * 0.55f)
    }

    LaunchedEffect(profile.id) {
        cardEnterComplete.set(false)
        offsetX.snapTo(0f)
        cardScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        )
        cardEnterComplete.set(true)

        val ch = swipeCommands
        while (ch.tryReceive().isSuccess) {
            continue // drain stale commands queued before enter completed
        }

        fun completeDismissal(liked: Boolean) {
            if (!cardConsumed.compareAndSet(false, true)) return
            onConsumedUpdated(liked)
        }
        while (true) {
            when (val cmd = ch.receive()) {
                is SwipeOffsetCommand.DragDelta -> {
                    if (!offsetX.isRunning) {
                        offsetX.snapTo(offsetX.value + cmd.dx)
                    }
                }

                SwipeOffsetCommand.PointerUp -> {
                    val combined = offsetX.value
                    val threshold =
                        containerWidthUpdated.coerceAtLeast(1f) * DismissThresholdFraction
                    if (abs(combined) <= threshold
                        || (cardConsumed.get())
                        || !cardDismissInFlight.compareAndSet(false, true)
                        ){
                        continue
                    }
                        try {
                            val liked = combined > 0f
                            offsetX.animateTo(
                                targetValue = exitTargetPx(liked),
                                animationSpec = tween(260, easing = FastOutSlowInEasing),
                            )
                            performSwipeCompleteHaptic(haptic)
                            completeDismissal(liked)
                        } finally {
                            cardDismissInFlight.set(false)
                        }
                }

                is SwipeOffsetCommand.ProgrammaticDismiss -> {
                    if (cardConsumed.get()) {
                        continue
                    }
                    if (!cardDismissInFlight.compareAndSet(false, true)) {
                        continue
                    }
                    try {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        offsetX.stop()
                        offsetX.snapTo(offsetX.value)
                        offsetX.animateTo(
                            targetValue = exitTargetPx(cmd.liked),
                            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
                        )
                        completeDismissal(cmd.liked)
                    } finally {
                        cardDismissInFlight.set(false)
                    }
                }
            }
        }
    }

    fun triggerProgrammaticDismiss(liked: Boolean) {
        if (!cardEnterComplete.get()) return
        if (cardDismissInFlight.get()) return
        if (cardConsumed.get()) return
        swipeCommands.trySend(SwipeOffsetCommand.ProgrammaticDismiss(liked))
    }

    Box(modifier = modifier.fillMaxSize()) {
        val swipeOffsetPx = offsetX.value
        val thresholdPx = (containerWidthPx * DismissThresholdFraction).coerceAtLeast(1f)
        val progressLeft = if (swipeOffsetPx < 0f) {
            (-swipeOffsetPx / thresholdPx).coerceIn(0f, 1f)
        } else {
            0f
        }
        val progressRight = if (swipeOffsetPx > 0f) {
            (swipeOffsetPx / thresholdPx).coerceIn(0f, 1f)
        } else {
            0f
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = bigGap)
                .onSizeChanged { containerWidthPx = it.width.toFloat() },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coords ->
                        val p = coords.positionInRoot()
                        cardHostLeftInRootPx = p.x
                        cardHostTopInRootPx = p.y
                    }
                    .drawWithContent {
                        drawContent()
                        if (progressLeft <= 0f && progressRight <= 0f) return@drawWithContent
                        translate(
                            left = -cardHostLeftInRootPx,
                            top = -cardHostTopInRootPx,
                        ) {
                            val w = screenWidthPx
                            val h = screenHeightPx
                            val radialRadius =
                                hypot(w.toDouble(), h.toDouble()).toFloat() * SwipeRadialRadiusKoef
                            // Center just outside bottom corner so iso-circles cut diagonally, not as horizontal bands.
                            val centerY = h + h * SwipeRadialCenterYKoef
                            if (progressLeft > 0f) {
                                val center = Offset(-w * SwipeRadialCenterXKoef, centerY)
                                drawRect(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            dislikeColor.copy(alpha = 0.55f * progressLeft),
                                            Color.Transparent,
                                        ),
                                        center = center,
                                        radius = radialRadius,
                                    ),
                                    topLeft = Offset.Zero,
                                    size = Size(w, h),
                                )
                            }
                            if (progressRight > 0f) {
                                val center = Offset(w + w * SwipeRadialCenterXKoef, centerY)
                                drawRect(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            likeColor.copy(alpha = 0.55f * progressRight),
                                            Color.Transparent,
                                        ),
                                        center = center,
                                        radius = radialRadius,
                                    ),
                                    topLeft = Offset.Zero,
                                    size = Size(w, h),
                                )
                            }
                        }
                    }
                    .graphicsLayer {
                        clip = false
                        translationX = offsetX.value
                    }
                    .pointerInput(profile.id) {
                        val initialPass = PointerEventPass.Initial
                        awaitEachGesture {
                            if (!cardEnterComplete.get() ||
                                offsetX.isRunning ||
                                cardScale.isRunning ||
                                cardDismissInFlight.get()
                            ) {
                                return@awaitEachGesture
                            }

                            val down = awaitFirstDown(requireUnconsumed = false, pass = initialPass)
                            var slopDx = 0f
                            val afterSlop = awaitHorizontalTouchSlopOrCancellation(down.id) { change, overSlop ->
                                slopDx += overSlop
                                change.consume()
                            }
                            if (afterSlop == null || !afterSlop.pressed) return@awaitEachGesture

                            if (slopDx != 0f) {
                                swipeCommands.trySend(SwipeOffsetCommand.DragDelta(slopDx))
                            }

                            var change: PointerInputChange? = afterSlop
                            while (change != null && change.pressed) {
                                val next = awaitHorizontalDragOrCancellation(change.id)
                                if (next != null && next.pressed) {
                                    val dx = next.positionChange().x
                                    next.consume()
                                    swipeCommands.trySend(SwipeOffsetCommand.DragDelta(dx))
                                }
                                change = next
                            }

                            swipeCommands.trySend(SwipeOffsetCommand.PointerUp)
                        }
                    },
            ) {
                UserFeedCard(
                    profile = profile,
                    onPass = { triggerProgrammaticDismiss(false) },
                    onLike = { triggerProgrammaticDismiss(true) },
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = cardScale.value
                            scaleY = cardScale.value
                            transformOrigin = TransformOrigin.Center
                        },
                )
            }
        }
    }
}

private fun performSwipeCompleteHaptic(haptic: HapticFeedback) {
    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
}
