package com.paranid5.crescendo.feature.splash.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.paranid5.crescendo.system.common.intent.openMainActivity
import kotlinx.coroutines.delay

private const val SplashScreenDelayMs = 1000L

@Composable
internal fun SwitchToMainEffect() {
    val context = LocalContext.current

    LaunchedEffect(context) {
        delay(SplashScreenDelayMs)
        context.openMainActivity()
    }
}