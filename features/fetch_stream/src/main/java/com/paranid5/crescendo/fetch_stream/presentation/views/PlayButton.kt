package com.paranid5.crescendo.fetch_stream.presentation.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.fetch_stream.domain.startStreaming
import com.paranid5.crescendo.fetch_stream.presentation.FetchStreamViewModel
import com.paranid5.crescendo.fetch_stream.presentation.properties.compose.collectCurrentTextAsState
import com.paranid5.crescendo.fetch_stream.presentation.properties.compose.collectIsConfirmButtonActiveAsState
import com.paranid5.crescendo.system.services.stream.StreamServiceAccessor
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingPagerState
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.ui.permissions.requests.audioRecordingPermissionsRequestLauncher
import com.paranid5.crescendo.ui.permissions.requests.foregroundServicePermissionsRequestLauncherCompat
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal fun PlayButton(modifier: Modifier = Modifier) {
    val isForegroundServicePermissionDialogShownState = remember { mutableStateOf(false) }
    val isAudioRecordingPermissionDialogShownState = remember { mutableStateOf(false) }

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

        PlayButtonImpl(
            areForegroundPermissionsGranted = areForegroundPermissionsGranted,
            isRecordingPermissionGranted = isRecordingPermissionGranted,
            launchFSPermissions = launchFSPermissions,
            launchRecordPermissions = launchRecordPermissions,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private inline fun PlayButtonImpl(
    areForegroundPermissionsGranted: Boolean,
    isRecordingPermissionGranted: Boolean,
    crossinline launchFSPermissions: () -> Unit,
    crossinline launchRecordPermissions: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FetchStreamViewModel = koinViewModel(),
    streamServiceAccessor: StreamServiceAccessor = koinInject()
) {
    val playingSheetState = LocalPlayingSheetState.current
    val playingPagerState = LocalPlayingPagerState.current

    val currentText by viewModel.collectCurrentTextAsState()
    val isConfirmButtonActive by viewModel.collectIsConfirmButtonActiveAsState()
    val coroutineScope = rememberCoroutineScope()

    Button(
        enabled = isConfirmButtonActive,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.background.alternative,
        ),
        content = { ButtonLabel(stringResource(R.string.play)) },
        onClick = {
            when {
                areForegroundPermissionsGranted.not() -> launchFSPermissions()

                isRecordingPermissionGranted.not() -> launchRecordPermissions()

                else -> coroutineScope.launch {
                    startStreaming(
                        publisher = viewModel,
                        streamServiceAccessor = streamServiceAccessor,
                        currentText = currentText,
                        playingPagerState = playingPagerState,
                        playingSheetState = playingSheetState,
                    )
                }
            }
        }
    )
}