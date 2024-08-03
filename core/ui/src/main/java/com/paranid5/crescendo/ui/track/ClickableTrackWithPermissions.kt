package com.paranid5.crescendo.ui.track

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.ui.permissions.requests.audioRecordingPermissionsRequestLauncher
import com.paranid5.crescendo.ui.permissions.requests.foregroundServicePermissionsRequestLauncherCompat
import com.paranid5.crescendo.ui.utils.clickableWithRipple

@Composable
fun Modifier.clickableTrackWithPermissions(
    permissionModifier: Modifier = Modifier,
    onClick: () -> Unit
): Modifier {
    val isFSPermissionDialogShownState = remember { mutableStateOf(false) }
    val isRecordingPermissionDialogShownState = remember { mutableStateOf(false) }

    val (areFSPermissionsGranted, launchFSPermissions) =
        foregroundServicePermissionsRequestLauncherCompat(
            isFGPermissionDialogShownState = isFSPermissionDialogShownState,
            modifier = permissionModifier,
        )

    val (isRecordingPermissionGranted, launchRecordPermissions) =
        audioRecordingPermissionsRequestLauncher(
            isRecordingPermissionDialogShownState = isRecordingPermissionDialogShownState,
            modifier = permissionModifier,
        )

    return this.clickableWithRipple(
        bounded = true,
        color = colors.selection.selected,
    ) {
        when {
            areFSPermissionsGranted.not() -> launchFSPermissions()
            isRecordingPermissionGranted.not() -> launchRecordPermissions()
            else -> onClick()
        }
    }
}
