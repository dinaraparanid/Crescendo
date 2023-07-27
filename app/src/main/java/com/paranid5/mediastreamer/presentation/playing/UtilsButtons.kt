package com.paranid5.mediastreamer.presentation.playing

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.paranid5.mediastreamer.EXTERNAL_STORAGE_PERMISSION_QUEUE
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.domain.stream_service.StreamServiceAccessor
import com.paranid5.mediastreamer.domain.track_service.TrackServiceAccessor
import com.paranid5.mediastreamer.presentation.composition_locals.LocalActivity
import com.paranid5.mediastreamer.presentation.composition_locals.LocalNavController
import com.paranid5.mediastreamer.presentation.ui.AudioStatus
import com.paranid5.mediastreamer.presentation.ui.extensions.getLightVibrantOrPrimary
import com.paranid5.mediastreamer.presentation.ui.extensions.openAppSettings
import com.paranid5.mediastreamer.presentation.ui.extensions.simpleShadow
import com.paranid5.mediastreamer.presentation.ui.permissions.PermissionDialog
import com.paranid5.mediastreamer.presentation.ui.permissions.description_providers.ExternalStorageDescriptionProvider
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import java.util.Queue

@Composable
internal fun UtilsButtons(palette: Palette?, modifier: Modifier = Modifier) =
    Row(modifier.fillMaxWidth()) {
        EqualizerButton(modifier = Modifier.weight(1F), palette = palette)
        RepeatButton(palette = palette, modifier = Modifier.weight(1F))
        LikeButton(modifier = Modifier.weight(1F), palette = palette)
        DownloadButton(modifier = Modifier.weight(1F), palette = palette)
    }

@Composable
private fun EqualizerButton(
    palette: Palette?,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject()
) {
    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val lightVibrantColor = palette.getLightVibrantOrPrimary()

    IconButton(
        modifier = modifier.simpleShadow(color = lightVibrantColor),
        onClick = { playingUIHandler.navigateToAudioEffects(context, navHostController) }
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(R.drawable.equalizer),
            contentDescription = stringResource(R.string.equalizer),
            tint = lightVibrantColor
        )
    }
}

@Composable
private fun RepeatButton(
    palette: Palette?,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject(),
    streamServiceAccessor: StreamServiceAccessor = koinInject(),
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) {
    val lightVibrantColor = palette.getLightVibrantOrPrimary()
    val isRepeating by storageHandler.isRepeatingState.collectAsState()
    val audioStatus by storageHandler.audioStatusState.collectAsState()

    IconButton(
        modifier = modifier.simpleShadow(color = lightVibrantColor),
        onClick = {
            when (audioStatus) {
                AudioStatus.STREAMING -> streamServiceAccessor.sendChangeRepeatBroadcast()
                AudioStatus.PLAYING -> trackServiceAccessor.sendChangeRepeatBroadcast()
                else -> Unit
            }
        }
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(if (isRepeating) R.drawable.repeat else R.drawable.no_repeat),
            contentDescription = stringResource(R.string.change_repeat),
            tint = lightVibrantColor
        )
    }
}

@Composable
private fun LikeButton(palette: Palette?, modifier: Modifier = Modifier) {
    val lightVibrantColor = palette.getLightVibrantOrPrimary()
    val isLiked by remember { mutableStateOf(false) }

    IconButton(
        modifier = modifier.simpleShadow(color = lightVibrantColor),
        onClick = { /** TODO: favourite database */ }
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(if (isLiked) R.drawable.like_filled else R.drawable.like),
            contentDescription = stringResource(R.string.favourites),
            tint = lightVibrantColor
        )
    }
}

@Composable
private fun DownloadButton(palette: Palette?, modifier: Modifier = Modifier) {
    val lightVibrantColor = palette.getLightVibrantOrPrimary()
    val activity = LocalActivity.current!!
    val isCashPropertiesDialogShownState = remember { mutableStateOf(false) }

    val externalStoragePermissionQueue = koinInject<Queue<String>>(
        named(EXTERNAL_STORAGE_PERMISSION_QUEUE)
    )

    val externalStorageDescriptionProvider = koinInject<ExternalStorageDescriptionProvider>()
    val notGrantedStoragePermissions = remember { mutableStateListOf<String>() }

    var areAllPermissionsGranted by remember {
        mutableStateOf(
            externalStoragePermissionQueue.all { permission ->
                ContextCompat.checkSelfPermission(activity, permission) ==
                        PackageManager.PERMISSION_GRANTED
            }
        )
    }

    val storagePermissionsResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsToGranted ->
        permissionsToGranted
            .asSequence()
            .filter { (_, isGranted) -> isGranted }
            .forEach { (permission, _) ->
                notGrantedStoragePermissions.remove(permission)
            }

        notGrantedStoragePermissions.addAll(
            permissionsToGranted
                .asSequence()
                .filter { (_, isGranted) -> !isGranted }
                .filter { (permission, _) -> permission !in notGrantedStoragePermissions }
                .map { (permission, _) -> permission }
        )

        areAllPermissionsGranted = notGrantedStoragePermissions.isEmpty()
    }

    Box(modifier) {
        IconButton(
            modifier = modifier.simpleShadow(color = lightVibrantColor),
            onClick = {
                storagePermissionsResultLauncher
                    .launch(externalStoragePermissionQueue.toTypedArray())
                isCashPropertiesDialogShownState.value = true
            }
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                painter = painterResource(R.drawable.save_icon),
                contentDescription = stringResource(R.string.download_as_mp3),
                tint = lightVibrantColor
            )
        }

        if (isCashPropertiesDialogShownState.value) {
            notGrantedStoragePermissions.forEach { permission ->
                PermissionDialog(
                    isDialogShownState = isCashPropertiesDialogShownState,
                    modifier = Modifier.align(Alignment.Center),
                    permissionDescriptionProvider = externalStorageDescriptionProvider,
                    isPermanentlyDeclined = when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                            !activity.shouldShowRequestPermissionRationale(permission)

                        else -> false
                    },
                    onGrantedClicked = {
                        storagePermissionsResultLauncher.launch(arrayOf(permission))
                        notGrantedStoragePermissions.removeFirst()
                    },
                    onGoToAppSettingsClicked = activity::openAppSettings
                )
            }

            if (areAllPermissionsGranted)
                CashPropertiesDialog(
                    isDialogShownState = isCashPropertiesDialogShownState,
                    modifier = Modifier.align(Alignment.Center)
                )
        }
    }
}