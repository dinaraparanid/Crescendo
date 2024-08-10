package com.paranid5.crescendo.feature.playing.presentation.ui.utils_buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.cache.presentation.CacheDialog
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.ui.permissions.requests.externalStoragePermissionsRequestLauncher
import com.paranid5.crescendo.utils.extensions.simpleShadow

private val IconSize = 32.dp

@Composable
internal fun DownloadButton(
    tint: Color,
    url: String,
    isLiveStreaming: Boolean,
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

        DownloadButtonImpl(
            isLiveStreaming = isLiveStreaming,
            paletteColor = tint,
            areStoragePermissionsGranted = areStoragePermissionsGranted,
            launchStoragePermissions = launchStoragePermissions,
            isCachePropertiesDialogShownState = isCachePropertiesDialogShownState,
            modifier = Modifier.align(Alignment.Center),
        )

        if (isCachePropertiesDialogShown && areStoragePermissionsGranted)
            CacheDialog(
                url = url,
                modifier = Modifier.align(Alignment.Center),
                hide = { isCachePropertiesDialogShown = false },
            )
    }
}

@Composable
private inline fun DownloadButtonImpl(
    isLiveStreaming: Boolean,
    paletteColor: Color,
    areStoragePermissionsGranted: Boolean,
    crossinline launchStoragePermissions: () -> Unit,
    isCachePropertiesDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    var isCachePropertiesDialogShown by isCachePropertiesDialogShownState

    IconButton(
        enabled = isLiveStreaming.not(),
        modifier = modifier.simpleShadow(color = paletteColor),
        onClick = {
            if (areStoragePermissionsGranted.not()) {
                launchStoragePermissions()
                return@IconButton
            }

            isCachePropertiesDialogShown = true
        }
    ) {
        DownloadIcon(
            paletteColor = paletteColor,
            modifier = Modifier.size(IconSize),
        )
    }
}

@Composable
private fun DownloadIcon(paletteColor: Color, modifier: Modifier = Modifier) =
    Icon(
        modifier = modifier,
        imageVector = ImageVector.vectorResource(R.drawable.ic_save),
        contentDescription = stringResource(R.string.cache_dialog_accept_button_title),
        tint = paletteColor,
    )
