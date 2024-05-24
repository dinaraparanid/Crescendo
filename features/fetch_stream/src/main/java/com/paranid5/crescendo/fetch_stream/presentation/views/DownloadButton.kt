package com.paranid5.crescendo.fetch_stream.presentation.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.cache.presentation.CacheDialog
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.fetch_stream.presentation.FetchStreamViewModel
import com.paranid5.crescendo.fetch_stream.presentation.properties.compose.collectCurrentTextAsState
import com.paranid5.crescendo.fetch_stream.presentation.properties.compose.collectIsConfirmButtonActiveAsState
import com.paranid5.crescendo.ui.permissions.requests.externalStoragePermissionsRequestLauncher
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun DownloadButton(
    modifier: Modifier = Modifier,
    viewModel: FetchStreamViewModel = koinViewModel(),
) {
    val text by viewModel.collectCurrentTextAsState()

    val isCachePropertiesDialogShownState = remember {
        mutableStateOf(false)
    }

    val isCachePropertiesDialogShown by isCachePropertiesDialogShownState

    Box(modifier) {
        val (areStoragePermissionsGranted, launchStoragePermissions) =
            externalStoragePermissionsRequestLauncher(
                isCachePropertiesDialogShownState,
                modifier = Modifier.align(Alignment.Center)
            )

        if (isCachePropertiesDialogShown && areStoragePermissionsGranted)
            CacheDialog(
                url = text,
                isDialogShownState = isCachePropertiesDialogShownState,
                modifier = Modifier.align(Alignment.Center)
            )

        DownloadButtonImpl(
            isCachePropertiesDialogShownState = isCachePropertiesDialogShownState,
            areStoragePermissionsGranted = areStoragePermissionsGranted,
            launchStoragePermissions = launchStoragePermissions,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
        )
    }
}

@Composable
private inline fun DownloadButtonImpl(
    isCachePropertiesDialogShownState: MutableState<Boolean>,
    areStoragePermissionsGranted: Boolean,
    crossinline launchStoragePermissions: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FetchStreamViewModel = koinViewModel(),
) {
    val colors = LocalAppColors.current
    val isConfirmButtonActive by viewModel.collectIsConfirmButtonActiveAsState()
    var isCachePropertiesDialogShown by isCachePropertiesDialogShownState

    Button(
        enabled = isConfirmButtonActive,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.backgroundAlternative
        ),
        content = { ButtonLabel(stringResource(R.string.download)) },
        onClick = {
            when {
                !areStoragePermissionsGranted -> launchStoragePermissions()
                else -> isCachePropertiesDialogShown = true
            }
        }
    )
}