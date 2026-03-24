package com.stuf.itinder.main.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.stuf.itinder.R

@Composable
fun RowActions(
    onPass: () -> Unit,
    onLike: () -> Unit,
    passCd: String,
    likeCd: String,
    modifier: Modifier = Modifier,
) {
    val pill = RoundedCornerShape(percent = 50)
    val btnH = dimensionResource(R.dimen.feed_card_button_height)
    val gap = dimensionResource(R.dimen.feed_card_button_gap)
    val iconSize = dimensionResource(R.dimen.feed_card_icon_in_button)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.feed_card_button_row_horizontal_padding),
                vertical = dimensionResource(R.dimen.feed_card_button_row_padding_top),
            ),
        horizontalArrangement = Arrangement.spacedBy(gap),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(btnH)
                .clip(pill)
                .background(colorResource(R.color.action_btn_dislike))
                .semantics { contentDescription = passCd }
                .clickable(onClick = onPass),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.solar_close_circle_linear),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = Color.White,
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(btnH)
                .clip(pill)
                .background(colorResource(R.color.action_btn_like))
                .semantics { contentDescription = likeCd }
                .clickable(onClick = onLike),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.solar_heart_bold),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = Color.White,
            )
        }
    }
}