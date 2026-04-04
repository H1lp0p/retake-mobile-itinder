package com.stuf.itinder.main

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import com.stuf.itinder.R
import kotlin.math.min

/** Mirrors `res/drawable/splash_gradient_bg` layer-list; layer-list drawables cannot use `painterResource`. */
@Composable
fun SplashGradientBackground(modifier: Modifier = Modifier) {
    val base = colorResource(R.color.gradient_bg)
    val purpleStart = colorResource(R.color.gradient_purple_start)
    val purpleEnd = colorResource(R.color.gradient_purple_end)
    val pinkStart = colorResource(R.color.gradient_pink_start)
    val pinkEnd = colorResource(R.color.gradient_pink_end)

    Canvas(modifier = modifier) {
        drawRect(color = base)
        val r = min(size.width, size.height)
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(purpleStart, purpleEnd),
                center = Offset(size.width * 0.9f, size.height * 0.1f),
                radius = r * 1.6f,
            ),
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(pinkStart, pinkEnd),
                center = Offset(size.width * 0.1f, size.height * 0.3f),
                radius = r * 1.2f,
            ),
        )
    }
}
