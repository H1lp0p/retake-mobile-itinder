package com.stuf.itinder.main.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.stuf.itinder.R

@Composable
fun ProfilePlaceholderScreen(modifier: Modifier = Modifier) {
    val cd = stringResource(R.string.main_placeholder_profile_cd)
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = cd },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = stringResource(R.string.main_placeholder_profile_label))
    }
}
