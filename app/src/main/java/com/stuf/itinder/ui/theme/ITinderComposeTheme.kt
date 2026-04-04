package com.stuf.itinder.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.stuf.itinder.R

private val InterBoldFontFamily = FontFamily(Font(R.font.inter_bold))

private fun Typography.withFontFamily(family: FontFamily): Typography {
    fun TextStyle.withFamily(): TextStyle = copy(fontFamily = family)
    return Typography(
        displayLarge = displayLarge.withFamily(),
        displayMedium = displayMedium.withFamily(),
        displaySmall = displaySmall.withFamily(),
        headlineLarge = headlineLarge.withFamily(),
        headlineMedium = headlineMedium.withFamily(),
        headlineSmall = headlineSmall.withFamily(),
        titleLarge = titleLarge.withFamily(),
        titleMedium = titleMedium.withFamily(),
        titleSmall = titleSmall.withFamily(),
        bodyLarge = bodyLarge.withFamily(),
        bodyMedium = bodyMedium.withFamily(),
        bodySmall = bodySmall.withFamily(),
        labelLarge = labelLarge.withFamily(),
        labelMedium = labelMedium.withFamily(),
        labelSmall = labelSmall.withFamily(),
    )
}

private val MainTypography = Typography().withFontFamily(InterBoldFontFamily)

private val MainDarkScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    secondary = Color.White,
    onSecondary = Color.Black,
    background = Color.Transparent,
    surface = Color.Transparent,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun ITinderComposeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MainDarkScheme,
        typography = MainTypography,
        content = content,
    )
}
