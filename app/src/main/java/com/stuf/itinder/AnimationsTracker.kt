package com.stuf.itinder

interface AnimationsTracker {
    fun onAnimationStart()
    fun onAnimationEnd()
}

object NoOpAnimationsTracker : AnimationsTracker {
    override fun onAnimationStart() = Unit
    override fun onAnimationEnd() = Unit
}

object AnimationsTrackerHolder {
    @Volatile
    var instance: AnimationsTracker = NoOpAnimationsTracker
}

