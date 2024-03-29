package com.paranid5.crescendo.fetch_stream.presentation.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.impl.presentation.composition_locals.playing.LocalPlayingPagerState
import com.paranid5.crescendo.core.impl.presentation.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.core.impl.presentation.permissions.requests.audioRecordingPermissionsRequestLauncher
import com.paranid5.crescendo.core.impl.presentation.permissions.requests.foregroundServicePermissionsRequestLauncherCompat
import com.paranid5.crescendo.fetch_stream.domain.FetchStreamInteractor
import com.paranid5.crescendo.fetch_stream.presentation.FetchStreamViewModel
import com.paranid5.crescendo.fetch_stream.presentation.properties.compose.collectCurrentTextAsState
import com.paranid5.crescendo.fetch_stream.presentation.properties.compose.collectIsConfirmButtonActiveAsState
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal fun ConfirmButton(modifier: Modifier = Modifier) {
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

        ConfirmButtonImpl(
            areForegroundPermissionsGranted = areForegroundPermissionsGranted,
            isRecordingPermissionGranted = isRecordingPermissionGranted,
            launchFSPermissions = launchFSPermissions,
            launchRecordPermissions = launchRecordPermissions,
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
    modifier: Modifier = Modifier,
    viewModel: FetchStreamViewModel = koinViewModel(),
    fetchStreamInteractor: FetchStreamInteractor = koinInject()
) {
    val colors = LocalAppColors.current
    val playingSheetState = LocalPlayingSheetState.current
    val playingPagerState = LocalPlayingPagerState.current

    val currentText by viewModel.collectCurrentTextAsState()
    val isConfirmButtonActive by viewModel.collectIsConfirmButtonActiveAsState()
    val coroutineScope = rememberCoroutineScope()

    Button(
        enabled = isConfirmButtonActive,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.backgroundAlternative
        ),
        content = { ConfirmButtonLabel() },
        onClick = {
            when {
                !areForegroundPermissionsGranted ->
                    launchFSPermissions()

                !isRecordingPermissionGranted ->
                    launchRecordPermissions()

                else -> coroutineScope.launch {
                    startStreaming(
                        currentText = currentText,
                        playingPagerState = playingPagerState,
                        playingSheetState = playingSheetState,
                        viewModel = viewModel,
                        fetchStreamInteractor = fetchStreamInteractor
                    )
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
private suspend fun startStreaming(
    currentText: String,
    playingPagerState: PagerState?,
    playingSheetState: BottomSheetScaffoldState?,
    viewModel: FetchStreamViewModel,
    fetchStreamInteractor: FetchStreamInteractor,
) {
    val url = currentText.trim()
    viewModel.setAudioStatus(AudioStatus.STREAMING)
    viewModel.setCurrentUrl(url)
    fetchStreamInteractor.startStreaming(url)
    playingPagerState?.animateScrollToPage(1)
    playingSheetState?.bottomSheetState?.expand()
}