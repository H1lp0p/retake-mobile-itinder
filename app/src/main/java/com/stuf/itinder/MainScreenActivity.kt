package com.stuf.itinder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.core.view.WindowCompat
import com.stuf.itinder.main.MainScreenRoot
import com.stuf.itinder.ui.theme.ITinderComposeTheme

class MainScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        val rootContentDescription = getString(R.string.main_screen_title)
        setContent {
            ITinderComposeTheme {
                Box(
                    Modifier
                        .fillMaxSize()
                        .semantics(mergeDescendants = false) {
                            contentDescription = rootContentDescription
                        },
                ) {
                    MainScreenRoot()
                }
            }
        }
    }

    companion object {
        /**
         * Clears the entire task so [MainScreenActivity] is the only activity;
         * back from it finishes the task (app closes).
         */
        fun createIntent(context: Context): Intent =
            Intent(context, MainScreenActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
    }
}
