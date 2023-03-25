package com.paranid5.mediastreamer.presentation.ui

import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.LocalNavController
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.appbar.AppBar
import com.paranid5.mediastreamer.presentation.appbar.stream_button.StreamButton
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun App() {
    val colors = LocalAppColors.current.value
    val navHostController = LocalNavController.current
    val currentScreenTitle by navHostController.currentRouteState.collectAsState()

    val coilPainter = rememberVideoCoverPainter(
        isPlaceholderRequired = false,
        BlurTransformation(LocalContext.current)
    )

    val backgroundColor = when (currentScreenTitle) {
        Screens.StreamScreen.Streaming.title -> Color.Transparent
        else -> colors.background
    }

    val animBackgroundColor = remember { Animatable(colors.background) }

    LaunchedEffect(backgroundColor) {
        animBackgroundColor.animateTo(backgroundColor, animationSpec = tween(500))
    }

    AnimatedContent(targetState = animBackgroundColor) { color ->
        Box(Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = coilPainter,
                contentDescription = stringResource(R.string.video_cover),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )

            Scaffold(
                floatingActionButton = { StreamButton() },
                floatingActionButtonPosition = FabPosition.Center,
                bottomBar = { AppBar() },
                content = { ContentScreen(padding = it) },
                modifier = Modifier.fillMaxSize(),
                containerColor = color.value
            )
        }
    }
}