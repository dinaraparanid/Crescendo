package com.paranid5.mediastreamer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.mediastreamer.ui.appbar.AppBar
import com.paranid5.mediastreamer.ui.appbar.HomeButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() = Scaffold(
    modifier = Modifier.fillMaxSize(),
    floatingActionButton = { HomeButton() },
    floatingActionButtonPosition = FabPosition.Center,
    bottomBar = { AppBar() },
    content = { ContentScreen(padding = it) }
)