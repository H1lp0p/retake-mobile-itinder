package com.stuf.itinder.utils

import android.view.HapticFeedbackConstants
import android.view.View

fun View.hapticClick() {
    performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
}
