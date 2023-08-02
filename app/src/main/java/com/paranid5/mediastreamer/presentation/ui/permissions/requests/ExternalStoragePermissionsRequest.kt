package com.paranid5.mediastreamer.presentation.ui.permissions.requests

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.paranid5.mediastreamer.EXTERNAL_STORAGE_PERMISSION_QUEUE
import com.paranid5.mediastreamer.presentation.ui.permissions.description_providers.ExternalStorageDescriptionProvider
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import java.util.Queue

@Composable
fun externalStoragePermissionsRequestLauncher(
    isExternalStoragePermissionDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier
): Pair<Boolean, () -> Unit> {
    val externalStoragePermissionQueue = koinInject<Queue<String>>(
        named(EXTERNAL_STORAGE_PERMISSION_QUEUE)
    )

    val externalStorageDescriptionProvider = koinInject<ExternalStorageDescriptionProvider>()

    return permissionsRequestLauncher(
        modifier = modifier,
        permissionQueue = externalStoragePermissionQueue,
        descriptionProvider = externalStorageDescriptionProvider,
        isPermissionDialogShownState = isExternalStoragePermissionDialogShownState,
    )
}