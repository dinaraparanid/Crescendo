package com.paranid5.crescendo.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.presentation.main.MainActivity
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    private companion object {
        private const val SPLASH_SCREEN_DELAY_MS = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LaunchedEffect(Unit) {
                delay(SPLASH_SCREEN_DELAY_MS)
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }

            SplashScreen(Modifier.fillMaxSize())
        }
    }
}