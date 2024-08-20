package com.paranid5.crescendo.feature.stream.fetch.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.paranid5.crescendo.cache.presentation.CacheDialog
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.feature.stream.fetch.view_model.FetchStreamState
import com.paranid5.crescendo.ui.permissions.requests.externalStoragePermissionsRequestLauncher

@Composable
internal fun DownloadButton(
    state: FetchStreamState,
    modifier: Modifier = Modifier,
) {
    val isCachePropertiesDialogShownState = remember { mutableStateOf(false) }
    var isCachePropertiesDialogShown by isCachePropertiesDialogShownState

    Box(modifier) {
        val (areStoragePermissionsGranted, launchStoragePermissions) =
            externalStoragePermissionsRequestLauncher(
                isExternalStoragePermissionDialogShownState = isCachePropertiesDialogShownState,
                modifier = Modifier.align(Alignment.Center),
            )

        if (isCachePropertiesDialogShown && areStoragePermissionsGranted)
            CacheDialog(
                url = state.url,
                modifier = Modifier.align(Alignment.Center),
                hide = { isCachePropertiesDialogShown = false },
            )

        UrlManagerButton(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.download),
            icon = ImageVector.vectorResource(R.drawable.ic_download),
            onClick = {
                when {
                    areStoragePermissionsGranted.not() -> launchStoragePermissions()
                    else -> isCachePropertiesDialogShown = true
                }
            },
        )
    }
}
