package com.paranid5.crescendo.presentation.main.playing.views.utils_buttons

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.presentation.main.playing.views.CacheDialog
import com.paranid5.crescendo.presentation.ui.extensions.getLightMutedOrPrimary
import com.paranid5.crescendo.presentation.ui.extensions.simpleShadow
import com.paranid5.crescendo.presentation.ui.permissions.requests.externalStoragePermissionsRequestLauncher

@Composable
fun DownloadButton(
    palette: Palette?,
    isLiveStreaming: Boolean,
    modifier: Modifier = Modifier
) {
    val paletteColor = palette.getLightMutedOrPrimary()

    val isCashPropertiesDialogShownState = remember {
        mutableStateOf(false)
    }

    val isCashPropertiesDialogShown by isCashPropertiesDialogShownState

    Box(modifier) {
        val (areStoragePermissionsGranted, launchStoragePermissions) =
            externalStoragePermissionsRequestLauncher(
                isCashPropertiesDialogShownState,
                modifier = Modifier.align(Alignment.Center)
            )

        DownloadButtonImpl(
            isLiveStreaming = isLiveStreaming,
            paletteColor = paletteColor,
            areStoragePermissionsGranted = areStoragePermissionsGranted,
            launchStoragePermissions = launchStoragePermissions,
            isCashPropertiesDialogShownState = isCashPropertiesDialogShownState,
            modifier = Modifier.align(Alignment.Center)
        )

        if (isCashPropertiesDialogShown && areStoragePermissionsGranted)
            CacheDialog(
                isDialogShownState = isCashPropertiesDialogShownState,
                modifier = Modifier.align(Alignment.Center)
            )
    }
}

@Composable
private inline fun DownloadButtonImpl(
    isLiveStreaming: Boolean,
    paletteColor: Color,
    areStoragePermissionsGranted: Boolean,
    crossinline launchStoragePermissions: () -> Unit,
    isCashPropertiesDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    var isCashPropertiesDialogShown by isCashPropertiesDialogShownState

    IconButton(
        enabled = !isLiveStreaming,
        modifier = modifier.simpleShadow(color = paletteColor),
        onClick = {
            if (!areStoragePermissionsGranted) {
                launchStoragePermissions()
                return@IconButton
            }

            isCashPropertiesDialogShown = true
        }
    ) {
        DownloadIcon(
            paletteColor = paletteColor,
            modifier = Modifier.size(30.dp),
        )
    }
}

@Composable
private fun DownloadIcon(paletteColor: Color, modifier: Modifier = Modifier) =
    Icon(
        modifier = modifier,
        painter = painterResource(R.drawable.save_icon),
        contentDescription = stringResource(R.string.download_as_mp3),
        tint = paletteColor
    )