package com.paranid5.crescendo.presentation.fetch_stream

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.StorageHandler
import com.paranid5.crescendo.presentation.Screens
import com.paranid5.crescendo.presentation.StateChangedCallback
import com.paranid5.crescendo.presentation.composition_locals.LocalPlayingSheetState
import com.paranid5.crescendo.presentation.ui.AudioStatus
import com.paranid5.crescendo.presentation.ui.OnUIStateChanged
import com.paranid5.crescendo.presentation.ui.permissions.requests.audioRecordingPermissionsRequestLauncher
import com.paranid5.crescendo.presentation.ui.permissions.requests.foregroundServicePermissionsRequestLauncher
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private val youtubeUrlRegex = Regex("https://(www\\.youtube\\.com/watch\\?v=|youtu\\.be/)\\S{11}")

@Composable
fun SearchStreamScreen(
    viewModel: FetchStreamViewModel,
    curScreenState: MutableStateFlow<Screens>,
    modifier: Modifier = Modifier
) {
    curScreenState.update { Screens.StreamFetching }

    val orientation = LocalConfiguration.current.orientation

    val inputText by viewModel
        .presenter
        .currentTextState
        .collectAsState(initial = "")

    val isConfirmButtonActive by remember {
        derivedStateOf { inputText?.matches(youtubeUrlRegex) == true }
    }

    val lifecycleOwnerState by rememberUpdatedState(LocalLifecycleOwner.current)

    OnUIStateChanged(
        lifecycleOwner = lifecycleOwnerState,
        StateChangedCallback(
            uiHandler = viewModel.handler,
            state = viewModel.isConfirmButtonPressedState,
            onDispose = { viewModel.finishUrlSetting() }
        ) {
            startStreaming(url = inputText!!)
            viewModel.finishUrlSetting()
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .let { mod ->
                when (orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> mod.padding(bottom = 15.dp)
                    else -> mod
                }
            }
    ) {
        Column(Modifier.align(Alignment.Center)) {
            Label(Modifier.padding(bottom = 10.dp))

            UrlEditor(
                inputText = inputText,
                viewModel = viewModel,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 10.dp)
            )

            ConfirmButton(
                isConfirmButtonActive,
                viewModel,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun Label(modifier: Modifier = Modifier) = Text(
    text = "${stringResource(R.string.enter_stream_url)}:",
    color = LocalAppColors.current.value.primary,
    modifier = modifier
)

@Composable
private fun UrlEditor(
    modifier: Modifier = Modifier,
    inputText: String?,
    hint: String = stringResource(R.string.your_url),
    viewModel: FetchStreamViewModel
) {
    TextField(
        value = inputText ?: "",
        singleLine = true,
        placeholder = { Text(hint) },
        onValueChange = { query -> viewModel.presenter.currentTextState.update { query } },
        modifier = modifier.width(300.dp),
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ConfirmButton(
    isConfirmButtonActive: Boolean,
    viewModel: FetchStreamViewModel,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject()
) {
    val playingSheetState = LocalPlayingSheetState.current
    val isForegroundServicePermissionDialogShownState = remember { mutableStateOf(false) }
    val isAudioRecordingPermissionDialogShownState = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(modifier) {
        val (areForegroundPermissionsGranted, launchFSPermissions) = foregroundServicePermissionsRequestLauncher(
            isForegroundServicePermissionDialogShownState,
            modifier = Modifier.align(Alignment.Center)
        )

        val (isRecordingPermissionGranted, launchRecordPermissions) = audioRecordingPermissionsRequestLauncher(
            isAudioRecordingPermissionDialogShownState,
            modifier = Modifier.align(Alignment.Center)
        )

        Button(
            enabled = isConfirmButtonActive,
            modifier = Modifier.align(Alignment.Center),
            onClick = {
                if (!areForegroundPermissionsGranted) {
                    launchFSPermissions()
                    return@Button
                }

                if (!isRecordingPermissionGranted) {
                    launchRecordPermissions()
                    return@Button
                }

                scope.launch {
                    storageHandler.storeAudioStatus(AudioStatus.STREAMING)
                    viewModel.onConfirmUrlButtonPressed()
                    playingSheetState?.bottomSheetState?.expand()
                }
            }
        ) {
            Text(stringResource(R.string.confirm))
        }
    }
}