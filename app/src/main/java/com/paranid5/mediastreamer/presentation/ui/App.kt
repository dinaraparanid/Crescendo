package com.paranid5.mediastreamer.presentation.ui

import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.StreamStates
import com.paranid5.mediastreamer.presentation.UpdateCheckerDialog
import com.paranid5.mediastreamer.presentation.appbar.AppBar
import com.paranid5.mediastreamer.presentation.appbar.stream_button.StreamButton
import com.paranid5.mediastreamer.presentation.ui.extensions.increaseDarkness
import com.paranid5.mediastreamer.presentation.ui.permissions.requests.externalStoragePermissionsRequestLauncher
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.compose.koinInject

@Composable
fun App(
    curScreenState: MutableStateFlow<Screens>,
    streamScreenState: StateFlow<StreamStates>,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current.value
    val curScreen by curScreenState.collectAsState()
    val isExternalStoragePermissionDialogShownState = remember { mutableStateOf(true) }

    val backgroundColor = when (curScreen) {
        Screens.Audio.Playing -> Color.Transparent
        else -> colors.background
    }

    val animBackgroundColor = remember { Animatable(colors.background) }

    LaunchedEffect(backgroundColor) {
        animBackgroundColor.animateTo(backgroundColor, animationSpec = tween(500))
    }

    AnimatedContent(targetState = animBackgroundColor, label = "") { color ->
        Box(modifier) {
            UpdateCheckerDialog(Modifier.align(Alignment.Center))

            val (areStoragePermissionsGranted, launchStoragePermissions) = externalStoragePermissionsRequestLauncher(
                isExternalStoragePermissionDialogShownState,
                modifier = Modifier.align(Alignment.Center)
            )

            LaunchedEffect(Unit) {
                if (!areStoragePermissionsGranted)
                    launchStoragePermissions()
            }

            BackgroundImage(Modifier.fillMaxSize())

            ScreenScaffold(
                curScreenState = curScreenState,
                streamScreenState = streamScreenState,
                animatedColor = color,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun BackgroundImage(
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject()
) {
    val config = LocalConfiguration.current
    val audioStatus by storageHandler.audioStatusState.collectAsState()

    val coilPainter = when (audioStatus) {
        AudioStatus.STREAMING -> rememberVideoCoverPainter(
            isPlaceholderRequired = true,
            size = config.screenWidthDp to config.screenHeightDp,
            isBlured = Build.VERSION.SDK_INT < Build.VERSION_CODES.S,
            bitmapSettings = Bitmap::increaseDarkness,
        )

        else -> rememberCurrentTrackCoverPainter(
            isPlaceholderRequired = true,
            size = config.screenWidthDp to config.screenHeightDp,
            isBlured = Build.VERSION.SDK_INT < Build.VERSION_CODES.S,
            bitmapSettings = Bitmap::increaseDarkness,
        )
    }

    Image(
        painter = coilPainter,
        modifier = modifier.blur(radius = 15.dp),
        contentDescription = stringResource(R.string.video_cover),
        contentScale = ContentScale.FillBounds,
        alignment = Alignment.Center,
    )
}

@Composable
private fun ScreenScaffold(
    curScreenState: MutableStateFlow<Screens>,
    streamScreenState: StateFlow<StreamStates>,
    animatedColor: Animatable<Color, AnimationVector4D>,
    modifier: Modifier = Modifier,
) {
    val config = LocalConfiguration.current

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
        modifier = modifier,
        containerColor = animatedColor.value
    )
}