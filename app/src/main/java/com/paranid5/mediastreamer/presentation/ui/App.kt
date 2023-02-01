package com.paranid5.mediastreamer.presentation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.mediastreamer.presentation.appbar.AppBar
import com.paranid5.mediastreamer.presentation.appbar.stream_button.StreamButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() = Scaffold(
    modifier = Modifier.fillMaxSize(),
    floatingActionButton = { StreamButton() },
    floatingActionButtonPosition = FabPosition.Center,
    bottomBar = { AppBar() },
    content = { ContentScreen(padding = it) }
)