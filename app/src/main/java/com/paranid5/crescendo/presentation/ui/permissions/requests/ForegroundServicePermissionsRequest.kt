package com.paranid5.crescendo.presentation.ui.permissions.requests

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.impl.di.FOREGROUND_SERVICE_PERMISSION_QUEUE
import com.paranid5.crescendo.presentation.ui.permissions.description_providers.ExternalStorageDescriptionProvider
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import java.util.Queue

@Composable
fun foregroundServicePermissionsRequestLauncherCompat(
    isFGPermissionDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
        foregroundServicePermissionsRequestLauncher(isFGPermissionDialogShownState, modifier)

    else -> true to {}
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun foregroundServicePermissionsRequestLauncher(
    isFGPermissionDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
): Pair<Boolean, () -> Unit> {
    val foregroundServicePermissionQueue = koinInject<Queue<String>>(
        named(FOREGROUND_SERVICE_PERMISSION_QUEUE)
    )

    val foregroundServiceDescriptionProvider = koinInject<ExternalStorageDescriptionProvider>()

    return permissionsRequestLauncher(
        modifier = modifier,
        permissionQueue = foregroundServicePermissionQueue,
        descriptionProvider = foregroundServiceDescriptionProvider,
        isPermissionDialogShownState = isFGPermissionDialogShownState,
    )
}