package com.stuf.itinder.main

object MainRoutes {
    const val FEED = "main_feed"
    const val PEOPLE = "main_people"
    const val CHATS = "main_chats"
    const val PROFILE = "main_profile"
    fun orderIndex(route: String?): Int = when (route) {
        FEED -> 0
        PEOPLE -> 1
        CHATS -> 2
        PROFILE -> 3
        else -> 0
    }
}
