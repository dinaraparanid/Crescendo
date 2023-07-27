package com.paranid5.mediastreamer.presentation.ui

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.paranid5.mediastreamer.AUDIO_RECORDING_PERMISSION_QUEUE
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.StreamStates
import com.paranid5.mediastreamer.presentation.UpdateCheckerDialog
import com.paranid5.mediastreamer.presentation.appbar.AppBar
import com.paranid5.mediastreamer.presentation.appbar.stream_button.StreamButton
import com.paranid5.mediastreamer.presentation.composition_locals.LocalActivity
import com.paranid5.mediastreamer.presentation.ui.extensions.increaseDarkness
import com.paranid5.mediastreamer.presentation.ui.extensions.openAppSettings
import com.paranid5.mediastreamer.presentation.ui.permissions.PermissionDialog
import com.paranid5.mediastreamer.presentation.ui.permissions.description_providers.AudioRecordingDescriptionProvider
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import java.util.Queue

@Composable
fun App(
    curScreenState: MutableStateFlow<Screens>,
    streamScreenState: StateFlow<StreamStates>,
    storageHandler: StorageHandler = koinInject()
) {
    val activity = LocalActivity.current
    val config = LocalConfiguration.current
    val colors = LocalAppColors.current.value
    val curScreen by curScreenState.collectAsState()
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

    val backgroundColor = when (curScreen) {
        Screens.Audio.Playing -> Color.Transparent
        else -> colors.background
    }

    val animBackgroundColor = remember { Animatable(colors.background) }

    val audioRecordingPermissionQueue = koinInject<Queue<String>>(
        named(AUDIO_RECORDING_PERMISSION_QUEUE)
    )

    val audioRecordingDescriptionProvider = koinInject<AudioRecordingDescriptionProvider>()
    val notGrantedRecordingPermissions = remember { mutableStateListOf<String>() }

    var areAllPermissionsGranted by remember {
        mutableStateOf(
            audioRecordingPermissionQueue.all { permission ->
                ContextCompat.checkSelfPermission(activity!!, permission) ==
                        PackageManager.PERMISSION_GRANTED
            }
        )
    }

    val isRecordingPermissionDialogShownState = remember { mutableStateOf(true) }

    val recordingPermissionsResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsToGranted ->
        permissionsToGranted
            .asSequence()
            .filter { (_, isGranted) -> isGranted }
            .forEach { (permission, _) ->
                notGrantedRecordingPermissions.remove(permission)
            }

        notGrantedRecordingPermissions.addAll(
            permissionsToGranted
                .asSequence()
                .filter { (_, isGranted) -> !isGranted }
                .filter { (permission, _) -> permission !in notGrantedRecordingPermissions }
                .map { (permission, _) -> permission }
        )

        areAllPermissionsGranted = notGrantedRecordingPermissions.isEmpty()
    }

    LaunchedEffect(Unit) {
        recordingPermissionsResultLauncher.launch(audioRecordingPermissionQueue.toTypedArray())
    }

    LaunchedEffect(backgroundColor) {
        animBackgroundColor.animateTo(backgroundColor, animationSpec = tween(500))
    }

    AnimatedContent(targetState = animBackgroundColor, label = "") { color ->
        Box(Modifier.fillMaxSize()) {
            UpdateCheckerDialog(Modifier.align(Alignment.Center))

            if (isRecordingPermissionDialogShownState.value)
                notGrantedRecordingPermissions.forEach { permission ->
                    PermissionDialog(
                        isDialogShownState = isRecordingPermissionDialogShownState,
                        modifier = Modifier.align(Alignment.Center),
                        permissionDescriptionProvider = audioRecordingDescriptionProvider,
                        isPermanentlyDeclined = when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                                !activity!!.shouldShowRequestPermissionRationale(permission)

                            else -> false
                        },
                        onGrantedClicked = {
                            recordingPermissionsResultLauncher.launch(arrayOf(permission))
                            notGrantedRecordingPermissions.removeFirst()
                        },
                        onGoToAppSettingsClicked = activity!!::openAppSettings,
                    )
                }

            Image(
                painter = coilPainter,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(radius = 15.dp),
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