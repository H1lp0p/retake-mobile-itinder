package com.stuf.itinder.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stuf.itinder.R

private val InterBoldFontFamily = FontFamily(Font(R.font.inter_bold))

private data class TabMeasure(val left: Float, val width: Float, val height: Float)

private fun LayoutCoordinates.positionInAncestor(ancestor: LayoutCoordinates): Offset {
    if (!isAttached || !ancestor.isAttached) return Offset.Zero
    return ancestor.windowToLocal(localToWindow(Offset.Zero))
}

private data class MainTabSpec(
    val route: String,
    val iconRes: Int,
    val labelRes: Int,
    val contentDescriptionRes: Int,
)

@Composable
fun MainFloatingBottomBar(
    selectedRoute: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = remember {
        listOf(
            MainTabSpec(
                MainRoutes.FEED,
                R.drawable.solar_feed_linear,
                R.string.main_tab_feed,
                R.string.main_tab_feed_cd,
            ),
            MainTabSpec(
                MainRoutes.PEOPLE,
                R.drawable.solar_users_group_rounded_linear,
                R.string.main_tab_people,
                R.string.main_tab_people_cd,
            ),
            MainTabSpec(
                MainRoutes.CHATS,
                R.drawable.solar_chat_round_line_linear,
                R.string.main_tab_chats,
                R.string.main_tab_chats_cd,
            ),
            MainTabSpec(
                MainRoutes.PROFILE,
                R.drawable.solar_user_circle_linear,
                R.string.main_tab_profile,
                R.string.main_tab_profile_cd,
            ),
        )
    }
    val selectedIndex = tabs.indexOfFirst { it.route == selectedRoute }.coerceIn(0, tabs.lastIndex)

    val density = LocalDensity.current
    val measures = remember { mutableStateMapOf<Int, TabMeasure>() }
    var trackCoordinates: LayoutCoordinates? by remember { mutableStateOf(null) }

    val inset = dimensionResource(R.dimen.main_bottom_bar_indicator_inset)
    val insetPx = with(density) { inset.toPx() }
    val selectedMeasure = measures[selectedIndex]

    val targetLeftDp = selectedMeasure?.let { m ->
        with(density) { (m.left + insetPx).toDp() }
    } ?: 0.dp
    val targetWidthDp = selectedMeasure?.let { m ->
        with(density) { (m.width - 2 * insetPx).coerceAtLeast(0f).toDp() }
    } ?: 0.dp
    val targetHeightDp = selectedMeasure?.let { m ->
        with(density) { m.height.toDp() }
    } ?: 0.dp

    val animatedLeft by animateDpAsState(
        targetValue = targetLeftDp,
        label = "indicatorLeft",
    )
    val animatedWidth by animateDpAsState(
        targetValue = targetWidthDp,
        label = "indicatorWidth",
    )
    val animatedHeight by animateDpAsState(
        targetValue = targetHeightDp,
        label = "indicatorHeight",
    )

    val tabFontStyle = TextStyle(
        fontFamily = InterBoldFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = Color.Black,
    )

    Box(
        modifier = modifier
            .wrapContentWidth()
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(colorResource(R.color.capsule_bg))
            .padding(
                horizontal = dimensionResource(R.dimen.min_gap),
                vertical = dimensionResource(R.dimen.min_gap),
            ),
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .onGloballyPositioned { cords ->
                    trackCoordinates = cords
                },
        ) {
            val track = trackCoordinates
            if (track != null && selectedMeasure != null && targetWidthDp > 0.dp) {
                val indicatorHeight = (animatedHeight - inset * 2).coerceAtLeast(1.dp)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = animatedLeft, y = 0.dp)
                        .width(animatedWidth)
                        .height(indicatorHeight)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(Color.White),
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.min_gap),
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabs.forEachIndexed { index, tab ->
                    val selected = index == selectedIndex
                    val tabCd = stringResource(tab.contentDescriptionRes)
                    val interactionSource = remember(tab.route) { MutableInteractionSource() }
                    Row(
                        modifier = Modifier
                            .onGloballyPositioned { coords ->
                                val t = trackCoordinates
                                if (t != null && coords.isAttached) {
                                    val pos: Offset = coords.positionInAncestor(t)
                                    measures[index] = TabMeasure(
                                        left = pos.x,
                                        width = coords.size.width.toFloat(),
                                        height = coords.size.height.toFloat(),
                                    )
                                }
                            }
                            .semantics {
                                role = Role.Tab
                                this.selected = selected
                                contentDescription = tabCd
                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                            ) {
                                onTabSelected(tab.route)
                            }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(tab.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(dimensionResource(R.dimen.main_bottom_bar_icon_size)),
                            tint = if (selected) Color.Black else Color.White,
                        )
                        AnimatedVisibility(
                            visible = selected,
                            enter = fadeIn() + expandHorizontally(),
                            exit = fadeOut() + shrinkHorizontally(),
                        ) {
                            Row {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(tab.labelRes),
                                    style = tabFontStyle,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
