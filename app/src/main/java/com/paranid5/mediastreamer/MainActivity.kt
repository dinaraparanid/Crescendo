package com.paranid5.mediastreamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import com.paranid5.mediastreamer.ui.App
import com.paranid5.mediastreamer.ui.screens.LocalNavController
import com.paranid5.mediastreamer.ui.screens.NavHostController
import com.paranid5.mediastreamer.ui.screens.Screens
import com.paranid5.mediastreamer.ui.theme.MediaStreamerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaStreamerTheme {
                val mainNavController = NavHostController(
                    value = rememberNavController(),
                    currentRouteState = Screens.Home.title
                )

                CompositionLocalProvider(LocalNavController provides mainNavController) {
                    App()
                }
            }
        }
    }
}