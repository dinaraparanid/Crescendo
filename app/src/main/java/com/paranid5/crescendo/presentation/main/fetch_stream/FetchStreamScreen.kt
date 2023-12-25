@file:Suppress("LongLine")

package com.paranid5.crescendo.presentation.main.fetch_stream

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.presentation.composition_locals.LocalPlayingPagerState
import com.paranid5.crescendo.presentation.composition_locals.LocalPlayingSheetState
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.presentation.ui.permissions.requests.audioRecordingPermissionsRequestLauncher
import com.paranid5.crescendo.presentation.ui.permissions.requests.foregroundServicePermissionsRequestLauncher
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.utils.DefaultOutlinedTextField
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private val youtubeUrlRegex = Regex(
    "https://((www\\.youtube\\.com/((watch\\?v=)|(live/)))|(youtu\\.be/))\\S{11}(\\?si=\\S{16})?(\\?feature=\\S+)?(&t=\\d+s)?"
)

@Composable
fun SearchStreamScreen(
    viewModel: FetchStreamViewModel,
    modifier: Modifier = Modifier,
) {
    val orientation = LocalConfiguration.current.orientation

    val inputText by viewModel
        .currentTextState
        .collectAsState(initial = "")

    val isConfirmButtonActive by remember {
        derivedStateOf { inputText.matches(youtubeUrlRegex) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                when (orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> Modifier.padding(bottom = 15.dp)
                    else -> Modifier
                }
            )
    ) {
        Column(Modifier.align(Alignment.Center)) {
            UrlEditor(
                inputText = inputText,
                viewModel = viewModel,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 10.dp)
                    .width(300.dp)
            )

            ConfirmButton(
                isConfirmButtonActive = isConfirmButtonActive,
                inputText = inputText,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun UrlEditor(
    inputText: String?,
    viewModel: FetchStreamViewModel,
    modifier: Modifier = Modifier,
) = DefaultOutlinedTextField(
    value = inputText ?: "",
    onValueChange = { query -> viewModel.setCurrentText(query) },
    label = {
        Text(
            text = stringResource(R.string.enter_stream_url),
            color = LocalAppColors.current.colorScheme.primary,
            fontSize = 12.sp,
        )
    },
    modifier = modifier
)

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun ConfirmButton(
    isConfirmButtonActive: Boolean,
    inputText: String,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject(),
    fetchStreamUIHandler: FetchStreamUIHandler = koinInject()
) {
    val colors = LocalAppColors.current.colorScheme
    val playingSheetState = LocalPlayingSheetState.current
    val playingPagerState = LocalPlayingPagerState.current

    val isForegroundServicePermissionDialogShownState = remember { mutableStateOf(false) }
    val isAudioRecordingPermissionDialogShownState = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(modifier) {
        val (areForegroundPermissionsGranted, launchFSPermissions) =
            foregroundServicePermissionsRequestLauncher(
                isForegroundServicePermissionDialogShownState,
                modifier = Modifier.align(Alignment.Center)
            )

        val (isRecordingPermissionGranted, launchRecordPermissions) =
            audioRecordingPermissionsRequestLauncher(
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
                    fetchStreamUIHandler.startStreaming(inputText.trim())
                    playingPagerState?.animateScrollToPage(1)
                    playingSheetState?.bottomSheetState?.expand()
                }
            }
        ) {
            Text(
                text = stringResource(R.string.confirm),
                color = colors.inverseSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}