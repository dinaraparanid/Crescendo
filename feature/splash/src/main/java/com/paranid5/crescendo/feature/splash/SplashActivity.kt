package com.paranid5.crescendo.feature.splash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.feature.splash.effect.SwitchToMainEffect

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SwitchToMainEffect()
            SplashScreen(Modifier.fillMaxSize())
        }
    }
}