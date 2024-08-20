package com.paranid5.crescendo.feature.stream.fetch.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.feature.stream.fetch.view_model.FetchStreamUiIntent
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingPagerState
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.ui.permissions.requests.audioRecordingPermissionsRequestLauncher
import com.paranid5.crescendo.ui.permissions.requests.foregroundServicePermissionsRequestLauncherCompat
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
internal fun PlayButton(
    onUiIntent: (FetchStreamUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val playingSheetState = LocalPlayingSheetState.current
    val playingPagerState = LocalPlayingPagerState.current

    val isForegroundServicePermissionDialogShownState = remember { mutableStateOf(false) }
    val isAudioRecordingPermissionDialogShownState = remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Box(modifier) {
        val (areForegroundPermissionsGranted, launchFSPermissions) =
            foregroundServicePermissionsRequestLauncherCompat(
                isForegroundServicePermissionDialogShownState,
                Modifier.align(Alignment.Center)
            )

        val (isRecordingPermissionGranted, launchRecordPermissions) =
            audioRecordingPermissionsRequestLauncher(
                isAudioRecordingPermissionDialogShownState,
                Modifier.align(Alignment.Center)
            )

        UrlManagerButton(
            modifier = modifier,
            title = stringResource(R.string.play),
            icon = ImageVector.vectorResource(R.drawable.ic_play_outlined),
            onClick = {
                when {
                    areForegroundPermissionsGranted.not() -> launchFSPermissions()
                    isRecordingPermissionGranted.not() -> launchRecordPermissions()
                    else -> coroutineScope.launch {
                        onUiIntent(FetchStreamUiIntent.Buttons.StartStreaming)
                        playingPagerState?.animateScrollToPage(1)
                        playingSheetState?.bottomSheetState?.expand()
                    }
                }
            },
        )
    }
}
