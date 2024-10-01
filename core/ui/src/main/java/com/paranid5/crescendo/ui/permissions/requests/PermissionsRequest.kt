package com.paranid5.crescendo.ui.permissions.requests

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.paranid5.crescendo.ui.permissions.PermissionDialog
import com.paranid5.crescendo.ui.permissions.description_providers.PermissionDescriptionProvider
import com.paranid5.crescendo.utils.extensions.getActivity
import com.paranid5.crescendo.utils.extensions.openAppSettings
import java.util.Queue

@Composable
fun permissionsRequestLauncher(
    permissionQueue: Queue<String>,
    descriptionProvider: PermissionDescriptionProvider,
    isPermissionDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier
): Pair<Boolean, () -> Unit> {
    val context = LocalContext.current
    val notGrantedPermissions = remember { mutableStateListOf<String>() }
    val areAllPermissionsGrantedState = rememberAllPermissionsGranted(permissionQueue)
    val areAllPermissionsGranted by areAllPermissionsGrantedState

    val permissionResultLauncher = rememberPermissionResultLauncher(
        notGrantedPermissions = notGrantedPermissions,
        areAllPermissionsGrantedState = areAllPermissionsGrantedState
    )

    val isPermissionDialogShown by isPermissionDialogShownState

    if (isPermissionDialogShown)
        notGrantedPermissions.forEach { permission ->
            PermissionDialog(
                permissionDescriptionProvider = descriptionProvider,
                isDialogShownState = isPermissionDialogShownState,
                modifier = modifier,
                isPermanentlyDeclined = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                        context.getActivity()
                            ?.shouldShowRequestPermissionRationale(permission) != true

                    else -> false
                },
                onGrantedClicked = {
                    permissionResultLauncher.launch(arrayOf(permission))
                    notGrantedPermissions.removeFirstOrNull()
                },
                onGoToAppSettingsClicked = context::openAppSettings,
            )
        }

    return areAllPermissionsGranted to {
        permissionResultLauncher.launch(permissionQueue.toTypedArray())
    }
}

@Composable
private fun rememberAllPermissionsGranted(permissionQueue: Queue<String>): MutableState<Boolean> {
    val context = LocalContext.current

    return remember {
        mutableStateOf(
            permissionQueue.all { permission ->
                ContextCompat.checkSelfPermission(context, permission) ==
                        PackageManager.PERMISSION_GRANTED
            }
        )
    }
}

@Composable
private fun rememberPermissionResultLauncher(
    notGrantedPermissions: SnapshotStateList<String>,
    areAllPermissionsGrantedState: MutableState<Boolean>
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
) { permissionsToGranted ->
    permissionsToGranted
        .asSequence()
        .filter { (_, isGranted) -> isGranted }
        .forEach { (permission, _) ->
            notGrantedPermissions.remove(permission)
        }

    notGrantedPermissions.addAll(
        permissionsToGranted
            .asSequence()
            .filter { (_, isGranted) -> !isGranted }
            .filter { (permission, _) -> permission !in notGrantedPermissions }
            .map { (permission, _) -> permission }
    )

    areAllPermissionsGrantedState.value = notGrantedPermissions.isEmpty()
}