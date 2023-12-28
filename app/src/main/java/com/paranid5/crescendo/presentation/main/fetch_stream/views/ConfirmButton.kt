package com.paranid5.crescendo.presentation.main.fetch_stream.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.presentation.composition_locals.playing.LocalPlayingPagerState
import com.paranid5.crescendo.presentation.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.presentation.main.fetch_stream.FetchStreamUIHandler
import com.paranid5.crescendo.presentation.main.fetch_stream.FetchStreamViewModel
import com.paranid5.crescendo.presentation.main.fetch_stream.properties.compose.collectCurrentTextAsState
import com.paranid5.crescendo.presentation.main.fetch_stream.properties.compose.collectIsConfirmButtonActiveAsState
import com.paranid5.crescendo.presentation.main.fetch_stream.properties.resetAudioStatusToStreaming
import com.paranid5.crescendo.presentation.ui.permissions.requests.audioRecordingPermissionsRequestLauncher
import com.paranid5.crescendo.presentation.ui.permissions.requests.foregroundServicePermissionsRequestLauncher
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun ConfirmButton(
    viewModel: FetchStreamViewModel,
    modifier: Modifier = Modifier,
) {
    val isForegroundServicePermissionDialogShownState = remember { mutableStateOf(false) }
    val isAudioRecordingPermissionDialogShownState = remember { mutableStateOf(false) }

    Box(modifier) {
        val (areForegroundPermissionsGranted, launchFSPermissions) =
            foregroundServicePermissionsRequestLauncher(
                isForegroundServicePermissionDialogShownState,
                Modifier.align(Alignment.Center)
            )

        val (isRecordingPermissionGranted, launchRecordPermissions) =
            audioRecordingPermissionsRequestLauncher(
                isAudioRecordingPermissionDialogShownState,
                Modifier.align(Alignment.Center)
            )

        ConfirmButtonImpl(
            areForegroundPermissionsGranted = areForegroundPermissionsGranted,
            isRecordingPermissionGranted = isRecordingPermissionGranted,
            launchFSPermissions = launchFSPermissions,
            launchRecordPermissions = launchRecordPermissions,
            viewModel = viewModel,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private inline fun ConfirmButtonImpl(
    areForegroundPermissionsGranted: Boolean,
    isRecordingPermissionGranted: Boolean,
    crossinline launchFSPermissions: () -> Unit,
    crossinline launchRecordPermissions: () -> Unit,
    viewModel: FetchStreamViewModel,
    modifier: Modifier = Modifier,
    fetchStreamUIHandler: FetchStreamUIHandler = koinInject()
) {
    val playingSheetState = LocalPlayingSheetState.current
    val playingPagerState = LocalPlayingPagerState.current

    val currentText by viewModel.collectCurrentTextAsState()
    val isConfirmButtonActive by viewModel.collectIsConfirmButtonActiveAsState()
    val coroutineScope = rememberCoroutineScope()

    Button(
        enabled = isConfirmButtonActive,
        modifier = modifier,
        content = { ConfirmButtonLabel() },
        onClick = {
            when {
                !areForegroundPermissionsGranted ->
                    launchFSPermissions()

                !isRecordingPermissionGranted ->
                    launchRecordPermissions()

                else -> coroutineScope.launch {
                    viewModel.resetAudioStatusToStreaming()
                    fetchStreamUIHandler.startStreaming(currentText.trim())
                    playingPagerState?.animateScrollToPage(1)
                    playingSheetState?.bottomSheetState?.expand()
                }
            }
        }
    )
}

@Composable
private fun ConfirmButtonLabel(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = stringResource(R.string.confirm),
        color = colors.fontColor,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}