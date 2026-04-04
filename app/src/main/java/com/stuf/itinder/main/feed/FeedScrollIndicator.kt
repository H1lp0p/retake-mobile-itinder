package com.stuf.itinder.main.feed

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.stuf.itinder.R

@Composable
fun FeedScrollIndicator(
    scrollState: ScrollState,
    trackHeight: Dp,
    thumbHeight: Dp,
    modifier: Modifier = Modifier,
    trackWidth: Dp = 4.dp,
    thumbWidth: Dp = 4.dp,
) {
    val trackColor = colorResource(R.color.feed_scrollbar_track)
    val thumbColor = colorResource(R.color.white)
    val laneWidth = maxOf(trackWidth, thumbWidth)

    Canvas(
        modifier = modifier
            .width(laneWidth)
            .height(trackHeight),
    ) {
        val trackW = trackWidth.toPx()
        val trackH = size.height
        val thumbW = thumbWidth.toPx()
        val cx = size.width / 2f
        val trackLeft = cx - trackW / 2f
        drawRoundRect(
            color = trackColor,
            topLeft = Offset(trackLeft, 0f),
            size = Size(trackW, trackH),
            cornerRadius = CornerRadius(trackW / 2f, trackW / 2f),
        )

        val maxScroll = scrollState.maxValue
        val thumbH = thumbHeight.toPx().coerceIn(0f, trackH)
        val travel = (trackH - thumbH).coerceAtLeast(0f)
        val thumbY = if (maxScroll <= 0) {
            0f
        } else {
            (scrollState.value / maxScroll.toFloat()) * travel
        }
        val thumbLeft = cx - thumbW / 2f
        drawRoundRect(
            color = thumbColor,
            topLeft = Offset(thumbLeft, thumbY),
            size = Size(thumbW, thumbH),
            cornerRadius = CornerRadius(thumbW / 2f, thumbW / 2f),
        )
    }
}
