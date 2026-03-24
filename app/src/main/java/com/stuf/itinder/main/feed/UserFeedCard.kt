package com.stuf.itinder.main.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stuf.itinder.R

@Composable
fun UserFeedCard(
    profile: FeedUserProfile,
    onPass: () -> Unit,
    onLike: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = remember(profile.id) { ScrollState(0) }
    val maxScroll = scrollState.maxValue
    val overlayProgress = if (maxScroll <= 0) {
        0f
    } else {
        (scrollState.value / maxScroll.toFloat()).coerceIn(0f, 1f)
    }

    val corner = RoundedCornerShape(dimensionResource(R.dimen.feed_card_corner_radius))
    val cardCd = stringResource(R.string.feed_user_card_cd)
    val passCd = stringResource(R.string.feed_action_pass_cd)
    val likeCd = stringResource(R.string.feed_action_like_cd)

    val minGap = dimensionResource(R.dimen.min_gap)
    val bigGap = dimensionResource(R.dimen.big_gap)
    val scrollbarTrackH = dimensionResource(R.dimen.feed_card_scrollbar_track_height)
    val scrollbarThumbH = dimensionResource(R.dimen.feed_card_scrollbar_thumb_height)

    Box(
        modifier = modifier
            .clip(corner)
            .semantics(mergeDescendants = false) {
                contentDescription = cardCd
            },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (profile.photoResId != 0) {
                Image(
                    painter = painterResource(profile.photoResId),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    colorResource(R.color.feed_photo_placeholder_top),
                                    colorResource(R.color.feed_photo_placeholder_bottom),
                                ),
                            ),
                        ),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f * overlayProgress)),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.5f * overlayProgress),
                            ),
                        ),
                    ),
            )

            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        var headerBlockHeight by remember(profile.id) { mutableStateOf(0.dp) }
                        val density = LocalDensity.current
                        val viewportHeight = maxHeight
                        val nameBottom = dimensionResource(R.dimen.feed_card_name_bottom_padding)
                        val chipsBottom = dimensionResource(R.dimen.feed_card_chips_bottom_padding)
                        val contentBottom = dimensionResource(R.dimen.feed_card_content_bottom_padding)

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState),
                        ) {
                            Spacer(
                                modifier = Modifier.height(
                                    (viewportHeight - headerBlockHeight).coerceAtLeast(0.dp),
                                ),
                            )
                            Column(
                                modifier = Modifier.onGloballyPositioned { coordinates ->
                                    headerBlockHeight = with(density) {
                                        coordinates.size.height.toDp()
                                    }
                                },
                            ) {
                                Text(
                                    text = profile.name,
                                    color = Color.White,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = bigGap),
                                )
                                Spacer(modifier = Modifier.height(nameBottom))
                                FlowRow(
                                    modifier = Modifier.padding(horizontal = bigGap),
                                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.feed_chip_gap_h)),
                                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.feed_chip_gap_v)),
                                ) {
                                    profile.tags.forEach { tag ->
                                        FeedTagChip(text = tag)
                                    }
                                }
                                Spacer(modifier = Modifier.height(chipsBottom))
                            }
                            Text(
                                text = profile.bio,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = bigGap),
                            )
                            Spacer(modifier = Modifier.height(contentBottom))
                        }
                    }
                }

                RowActions(
                    onPass = onPass,
                    onLike = onLike,
                    passCd = passCd,
                    likeCd = likeCd,
                    modifier = Modifier.padding(bottom = minGap),
                )
            }

            FeedScrollIndicator(
                scrollState = scrollState,
                trackHeight = scrollbarTrackH,
                thumbHeight = scrollbarThumbH,
                trackWidth = dimensionResource(R.dimen.feed_card_scrollbar_track_width),
                thumbWidth = dimensionResource(R.dimen.feed_card_scrollbar_thumb_width),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = dimensionResource(R.dimen.feed_card_scrollbar_end_padding)),
            )
        }
    }
}

@Composable
private fun FeedTagChip(text: String) {
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

@Composable
private fun RowActions(
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
