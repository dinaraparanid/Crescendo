package com.paranid5.mediastreamer.presentation.appbar.stream_button

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.StreamStates
import com.paranid5.mediastreamer.presentation.composition_locals.LocalNavController
import com.paranid5.mediastreamer.presentation.nextState
import com.paranid5.mediastreamer.presentation.ui.permissions.requests.audioRecordingPermissionsRequestLauncher
import com.paranid5.mediastreamer.presentation.ui.permissions.requests.foregroundServicePermissionsRequestLauncher
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import kotlinx.coroutines.flow.StateFlow
import org.koin.compose.koinInject

@SuppressLint("NewApi")
@Composable
fun StreamButton(
    streamScreenState: StateFlow<StreamStates>,
    modifier: Modifier = Modifier,
    streamButtonUIHandler: StreamButtonUIHandler = koinInject()
) {
    val navHostController = LocalNavController.current
    val streamCompose by streamScreenState.collectAsState()

    val isForegroundServicePermissionDialogShownState = remember { mutableStateOf(false) }
    val isAudioRecordingPermissionDialogShownState = remember { mutableStateOf(false) }

    Box(modifier) {
        val (areForegroundPermissionsGranted, launchFSPermissions) = foregroundServicePermissionsRequestLauncher(
            isForegroundServicePermissionDialogShownState,
            modifier = Modifier.align(Alignment.Center)
        )

        val (isRecordingPermissionGranted, launchRecordPermissions) = audioRecordingPermissionsRequestLauncher(
            isAudioRecordingPermissionDialogShownState,
            modifier = Modifier.align(Alignment.Center)
        )

        FloatingActionButton(
            modifier = modifier,
            onClick = {
                if (!areForegroundPermissionsGranted) {
                    launchFSPermissions()
                    return@FloatingActionButton
                }

                if (!isRecordingPermissionGranted) {
                    launchRecordPermissions()
                    return@FloatingActionButton
                }

                streamButtonUIHandler.navigateToStream(
                    navHostController = navHostController,
                    nextStreamState = streamScreenState.value.nextState
                )
            }
        ) {
            when (streamCompose) {
                StreamStates.STREAMING -> Icon(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = stringResource(id = R.string.home),
                    tint = LocalAppColors.current.value.primary,
                    modifier = Modifier.size(30.dp)
                )

                StreamStates.SEARCHING -> Icon(
                    painter = painterResource(id = R.drawable.stream_icon),
                    contentDescription = stringResource(id = R.string.home),
                    tint = LocalAppColors.current.value.primary,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}