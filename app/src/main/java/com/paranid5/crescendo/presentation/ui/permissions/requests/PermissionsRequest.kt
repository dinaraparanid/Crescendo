package com.paranid5.crescendo.presentation.ui.permissions.requests

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.paranid5.crescendo.presentation.composition_locals.LocalActivity
import com.paranid5.crescendo.presentation.ui.extensions.openAppSettings
import com.paranid5.crescendo.presentation.ui.permissions.PermissionDialog
import com.paranid5.crescendo.presentation.ui.permissions.description_providers.PermissionDescriptionProvider
import java.util.Queue

@Composable
fun permissionsRequestLauncher(
    permissionQueue: Queue<String>,
    descriptionProvider: PermissionDescriptionProvider,
    isPermissionDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier
): Pair<Boolean, () -> Unit> {
    val activity = LocalActivity.current
    val notGrantedPermissions = remember { mutableStateListOf<String>() }

    var areAllPermissionsGranted by remember {
        mutableStateOf(
            permissionQueue.all { permission ->
                ContextCompat.checkSelfPermission(activity!!, permission) ==
                        PackageManager.PERMISSION_GRANTED
            }
        )
    }

    val permissionResultLauncher = rememberLauncherForActivityResult(
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

        areAllPermissionsGranted = notGrantedPermissions.isEmpty()
    }

    if (isPermissionDialogShownState.value)
        notGrantedPermissions.forEach { permission ->
            PermissionDialog(
                isDialogShownState = isPermissionDialogShownState,
                modifier = modifier,
                permissionDescriptionProvider = descriptionProvider,
                isPermanentlyDeclined = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                        !activity!!.shouldShowRequestPermissionRationale(permission)

                    else -> false
                },
                onGrantedClicked = {
                    permissionResultLauncher.launch(arrayOf(permission))
                    notGrantedPermissions.removeFirst()
                },
                onGoToAppSettingsClicked = activity!!::openAppSettings,
            )
        }

    return areAllPermissionsGranted to {
        permissionResultLauncher.launch(permissionQueue.toTypedArray())
    }
}