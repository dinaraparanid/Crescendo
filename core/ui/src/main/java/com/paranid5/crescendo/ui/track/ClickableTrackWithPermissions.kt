package com.paranid5.crescendo.ui.track

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.ui.permissions.requests.audioRecordingPermissionsRequestLauncher
import com.paranid5.crescendo.ui.permissions.requests.foregroundServicePermissionsRequestLauncherCompat

@Composable
inline fun Modifier.clickableTrackWithPermissions(
    crossinline onClick: () -> Unit,
    permissionModifier: Modifier = Modifier
): Modifier {
    val isFSPermissionDialogShownState = remember { mutableStateOf(false) }
    val isRecordingPermissionDialogShownState = remember { mutableStateOf(false) }

    val (areFSPermissionsGranted, launchFSPermissions) =
        foregroundServicePermissionsRequestLauncherCompat(
            isFSPermissionDialogShownState,
            permissionModifier
        )

    val (isRecordingPermissionGranted, launchRecordPermissions) =
        audioRecordingPermissionsRequestLauncher(
            isRecordingPermissionDialogShownState,
            permissionModifier
        )

    return this.clickable {
        when {
            !areFSPermissionsGranted -> launchFSPermissions()
            !isRecordingPermissionGranted -> launchRecordPermissions()
            else -> onClick()
        }
    }
}