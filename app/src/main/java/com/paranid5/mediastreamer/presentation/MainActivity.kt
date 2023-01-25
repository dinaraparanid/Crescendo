package com.paranid5.mediastreamer.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import com.paranid5.mediastreamer.presentation.composition_locals.LocalStreamState
import com.paranid5.mediastreamer.presentation.composition_locals.StreamState
import com.paranid5.mediastreamer.presentation.composition_locals.StreamStates
import com.paranid5.mediastreamer.presentation.ui.App
import com.paranid5.mediastreamer.presentation.ui.screens.LocalNavController
import com.paranid5.mediastreamer.presentation.ui.screens.NavHostController
import com.paranid5.mediastreamer.presentation.ui.screens.Screens
import com.paranid5.mediastreamer.presentation.ui.theme.MediaStreamerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaStreamerTheme {
                val mainNavController = NavHostController(
                    value = rememberNavController(),
                    currentRouteState = Screens.StreamScreen.Searching.title
                )

                CompositionLocalProvider(
                    LocalNavController provides mainNavController,
                    LocalStreamState provides StreamState(StreamStates.SEARCHING)
                ) {
                    App()
                }
            }
        }
    }
}