package com.stuf.itinder.main.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.stuf.itinder.R
import com.stuf.itinder.main.feed.FeedUserProfile
import com.stuf.itinder.main.feed.SwipeableFeedCardHost

@Composable
fun FeedPlaceholderScreen(modifier: Modifier = Modifier) {
    val cd = stringResource(R.string.main_placeholder_feed_cd)
    val demoBio = stringResource(R.string.feed_demo_bio)
    val nameAndrey = stringResource(R.string.feed_demo_name)
    val tagPython = stringResource(R.string.feed_demo_tag_python)
    val tagDjango = stringResource(R.string.feed_demo_tag_django)
    val tagRest = stringResource(R.string.feed_demo_tag_rest)
    val nameGigachad = stringResource(R.string.feed_demo_gigachad_name)
    val tagGym = stringResource(R.string.feed_demo_gigachad_tag_gym)
    val tagHealth = stringResource(R.string.feed_demo_gigachad_tag_health)
    val tagMemes = stringResource(R.string.feed_demo_gigachad_tag_memes)
    val nameJojo = stringResource(R.string.feed_demo_jojo_name)
    val tagJava = stringResource(R.string.feed_demo_jojo_tag_java)
    val tagStand = stringResource(R.string.feed_demo_jojo_tag_stand)
    val tagCaps = stringResource(R.string.feed_demo_jojo_tag_caps)
    val nameMaria = stringResource(R.string.feed_demo_maria_name)
    val tagKotlin = stringResource(R.string.feed_demo_maria_tag_kotlin)
    val tagCompose = stringResource(R.string.feed_demo_maria_tag_compose)
    val tagAndroid = stringResource(R.string.feed_demo_maria_tag_android)
    val feedEndLabel = stringResource(R.string.feed_demo_feed_end)

    val profiles = remember(
        demoBio,
        nameAndrey,
        tagPython,
        tagDjango,
        tagRest,
        nameGigachad,
        tagGym,
        tagHealth,
        tagMemes,
        nameJojo,
        tagJava,
        tagStand,
        tagCaps,
        nameMaria,
        tagKotlin,
        tagCompose,
        tagAndroid,
    ) {
        mutableStateListOf(
            FeedUserProfile(
                id = "p_andrey",
                name = nameAndrey,
                tags = listOf(tagPython, tagDjango, tagRest),
                bio = demoBio,
                photoResId = R.drawable.andrey_asset,
            ),
            FeedUserProfile(
                id = "p_gigachad",
                name = nameGigachad,
                tags = listOf(tagGym, tagHealth, tagMemes),
                bio = demoBio,
                photoResId = R.drawable.gigachad_asset,
            ),
            FeedUserProfile(
                id = "p_jojo",
                name = nameJojo,
                tags = listOf(tagJava, tagStand, tagCaps),
                bio = demoBio,
                photoResId = R.drawable.jojo_asset,
            ),
            FeedUserProfile(
                id = "p_maria",
                name = nameMaria,
                tags = listOf(tagKotlin, tagCompose, tagAndroid),
                bio = demoBio,
                photoResId = R.drawable.andrey_asset,
            ),
        )
    }

    var selectedCardInd by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = cd },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = dimensionResource(R.dimen.min_gap),
                    bottom = dimensionResource(R.dimen.big_gap),
                )
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.big_gap))
                    .width(dimensionResource(R.dimen.main_logo_width)),
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.big_gap)))
            if (profiles.isEmpty()) {
                Text(
                    text = feedEndLabel,
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.big_gap)),
                )
            } else {
                val current = profiles[selectedCardInd % profiles.size]
                SwipeableFeedCardHost(
                    profile = current,
                    onConsumed = { selectedCardInd += 1 },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            }
        }
    }
}
