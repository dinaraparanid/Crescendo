package com.paranid5.crescendo.presentation.splash.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.paranid5.crescendo.presentation.ui.extensions.openMainActivity
import kotlinx.coroutines.delay

private const val SPLASH_SCREEN_DELAY_MS = 1000L

@Composable
fun SwitchToMainEffect() {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(SPLASH_SCREEN_DELAY_MS)
        context.openMainActivity()
    }
}