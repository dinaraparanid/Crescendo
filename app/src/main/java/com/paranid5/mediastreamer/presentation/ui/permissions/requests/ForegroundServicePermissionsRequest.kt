package com.paranid5.mediastreamer.presentation.ui.permissions.requests

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.paranid5.mediastreamer.FOREGROUND_SERVICE_PERMISSION_QUEUE
import com.paranid5.mediastreamer.presentation.ui.permissions.description_providers.ExternalStorageDescriptionProvider
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import java.util.Queue

@Composable
fun foregroundServicePermissionsRequestLauncher(
    isForegroundServicePermissionDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier
): Pair<Boolean, () -> Unit> {
    val foregroundServicePermissionQueue = koinInject<Queue<String>>(
        named(FOREGROUND_SERVICE_PERMISSION_QUEUE)
    )

    val foregroundServiceDescriptionProvider = koinInject<ExternalStorageDescriptionProvider>()

    return permissionsRequestLauncher(
        modifier = modifier,
        permissionQueue = foregroundServicePermissionQueue,
        descriptionProvider = foregroundServiceDescriptionProvider,
        isPermissionDialogShownState = isForegroundServicePermissionDialogShownState,
    )
}