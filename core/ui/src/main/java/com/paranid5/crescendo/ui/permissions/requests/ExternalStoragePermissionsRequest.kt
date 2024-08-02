package com.paranid5.crescendo.ui.permissions.requests

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.impl.di.EXTERNAL_STORAGE_PERMISSION_QUEUE
import com.paranid5.crescendo.ui.permissions.description_providers.ExternalStorageDescriptionProvider
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import java.util.Queue

@Composable
fun externalStoragePermissionsRequestLauncher(
    isExternalStoragePermissionDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    externalStoragePermissionQueue: Queue<String> = koinInject(
        named(EXTERNAL_STORAGE_PERMISSION_QUEUE)
    ),
    externalStorageDescriptionProvider: ExternalStorageDescriptionProvider = koinInject(),
): Pair<Boolean, () -> Unit> = permissionsRequestLauncher(
    modifier = modifier,
    permissionQueue = externalStoragePermissionQueue,
    descriptionProvider = externalStorageDescriptionProvider,
    isPermissionDialogShownState = isExternalStoragePermissionDialogShownState,
)
