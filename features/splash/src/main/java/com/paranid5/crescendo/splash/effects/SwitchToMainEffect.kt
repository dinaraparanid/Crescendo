package com.paranid5.crescendo.splash.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.paranid5.crescendo.system.common.intent.openMainActivity
import kotlinx.coroutines.delay

private const val SPLASH_SCREEN_DELAY_MS = 1000L

@Composable
internal fun SwitchToMainEffect() {
    val context = LocalContext.current

    LaunchedEffect(context) {
        delay(SPLASH_SCREEN_DELAY_MS)
        context.openMainActivity()
    }
}