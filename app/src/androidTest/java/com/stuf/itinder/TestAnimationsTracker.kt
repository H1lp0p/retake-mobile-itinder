package com.stuf.itinder

import androidx.test.espresso.idling.CountingIdlingResource

object TestAnimationsTracker : AnimationsTracker {

    @JvmField
    val idling = CountingIdlingResource("Animations")

    override fun onAnimationStart() {
        idling.increment()
    }

    override fun onAnimationEnd() {
        if (!idling.isIdleNow) {
            idling.decrement()
        }
    }
}

