package com.stuf.itinder.utils

object NavKeys {
    const val isFromSplashKey = "from_splash"
    const val introSourceKey = "intro_source"
    enum class IntroSource(val source: String) {
        Register("register"),
        Login("login")
    }
}
