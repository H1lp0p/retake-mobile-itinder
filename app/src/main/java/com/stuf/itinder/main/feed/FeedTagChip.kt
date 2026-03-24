package com.stuf.itinder.main.feed

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.sp
import com.stuf.itinder.R

@Composable
fun FeedTagChip(text: String) {
    Surface(
        shape = RoundedCornerShape(percent = 50),
        color = colorResource(R.color.feed_chip_background),
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp,
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.feed_chip_h_padding),
                vertical = dimensionResource(R.dimen.feed_chip_v_padding),
            ),
        )
    }
}