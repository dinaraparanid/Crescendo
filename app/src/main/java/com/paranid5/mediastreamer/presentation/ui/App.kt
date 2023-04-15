package com.paranid5.mediastreamer.presentation.ui

import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.StreamStates
import com.paranid5.mediastreamer.presentation.appbar.AppBar
import com.paranid5.mediastreamer.presentation.appbar.stream_button.StreamButton
import com.paranid5.mediastreamer.presentation.ui.extensions.increaseDarkness
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun App(curScreenState: MutableStateFlow<Screens>, streamScreenState: StateFlow<StreamStates>) {
    val config = LocalConfiguration.current
    val colors = LocalAppColors.current.value
    val curScreen by curScreenState.collectAsState()

    val coilPainter = rememberVideoCoverPainter(
        isPlaceholderRequired = true,
        size = config.screenWidthDp to config.screenHeightDp,
        isBlured = Build.VERSION.SDK_INT < Build.VERSION_CODES.S,
        bitmapSettings = Bitmap::increaseDarkness,
    )

    val backgroundColor = when (curScreen) {
        Screens.StreamScreen.Streaming -> Color.Transparent
        else -> colors.background
    }

    val animBackgroundColor = remember { Animatable(colors.background) }

    LaunchedEffect(backgroundColor) {
        animBackgroundColor.animateTo(backgroundColor, animationSpec = tween(500))
    }

    AnimatedContent(targetState = animBackgroundColor) { color ->
        Box(Modifier.fillMaxSize()) {
            Image(
                painter = coilPainter,
                modifier = Modifier.fillMaxSize().blur(radius = 15.dp),
                contentDescription = stringResource(R.string.video_cover),
                contentScale = ContentScale.FillBounds,
                alignment = Alignment.Center,
            )

            Scaffold(
                floatingActionButton = {
                    when (config.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> StreamButton(
                            modifier = Modifier.padding(end = 10.dp),
                            streamScreenState = streamScreenState
                        )

                        else -> StreamButton(streamScreenState)
                    }
                },
                floatingActionButtonPosition = when (config.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> FabPosition.End
                    else -> FabPosition.Center
                },
                bottomBar = { AppBar() },
                content = { ContentScreen(padding = it, curScreenState) },
                modifier = Modifier.fillMaxSize(),
                containerColor = color.value
            )
        }
    }
}