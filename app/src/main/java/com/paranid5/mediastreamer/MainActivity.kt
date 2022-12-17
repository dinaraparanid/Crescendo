package com.paranid5.mediastreamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.paranid5.mediastreamer.ui.appbar.AppBar
import com.paranid5.mediastreamer.ui.theme.MediaStreamerTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaStreamerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { AppBar() }
                ) {
                    it
                }
            }
        }
    }
}