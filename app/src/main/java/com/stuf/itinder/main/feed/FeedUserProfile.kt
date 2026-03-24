package com.stuf.itinder.main.feed

import androidx.annotation.DrawableRes

data class FeedUserProfile(
    val id: String,
    val name: String,
    val tags: List<String>,
    val bio: String,
    @DrawableRes val photoResId: Int = 0,
)
