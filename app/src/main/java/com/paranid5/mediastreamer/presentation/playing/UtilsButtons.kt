package com.paranid5.mediastreamer.presentation.playing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.domain.services.stream_service.StreamServiceAccessor
import com.paranid5.mediastreamer.domain.services.track_service.TrackServiceAccessor
import com.paranid5.mediastreamer.presentation.composition_locals.LocalNavController
import com.paranid5.mediastreamer.presentation.ui.AudioStatus
import com.paranid5.mediastreamer.presentation.ui.extensions.getLightMutedOrPrimary
import com.paranid5.mediastreamer.presentation.ui.extensions.simpleShadow
import com.paranid5.mediastreamer.presentation.ui.permissions.requests.externalStoragePermissionsRequestLauncher
import org.koin.compose.koinInject

@Composable
internal fun UtilsButtons(palette: Palette?, modifier: Modifier = Modifier) =
    Row(modifier.fillMaxWidth()) {
        EqualizerButton(palette, Modifier.weight(1F))
        RepeatButton(palette, Modifier.weight(1F))
        LikeButton(palette, Modifier.weight(1F))
        PlaylistOrDownloadButton(palette, Modifier.weight(1F))
    }

@Composable
private fun EqualizerButton(
    palette: Palette?,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject()
) {
    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val paletteColor = palette.getLightMutedOrPrimary()

    IconButton(
        modifier = modifier.simpleShadow(color = paletteColor),
        onClick = { playingUIHandler.navigateToAudioEffects(context, navHostController) }
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(R.drawable.equalizer),
            contentDescription = stringResource(R.string.equalizer),
            tint = paletteColor
        )
    }
}

@Composable
private fun RepeatButton(
    palette: Palette?,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject(),
    streamServiceAccessor: StreamServiceAccessor = koinInject(),
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) {
    val paletteColor = palette.getLightMutedOrPrimary()
    val isRepeating by storageHandler.isRepeatingState.collectAsState()
    val audioStatus by storageHandler.audioStatusState.collectAsState()

    IconButton(
        modifier = modifier.simpleShadow(color = paletteColor),
        onClick = {
            when (audioStatus) {
                AudioStatus.STREAMING -> streamServiceAccessor.sendChangeRepeatBroadcast()
                AudioStatus.PLAYING -> trackServiceAccessor.sendChangeRepeatBroadcast()
                else -> Unit
            }
        }
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(if (isRepeating) R.drawable.repeat else R.drawable.no_repeat),
            contentDescription = stringResource(R.string.change_repeat),
            tint = paletteColor
        )
    }
}

@Composable
private fun LikeButton(palette: Palette?, modifier: Modifier = Modifier) {
    val paletteColor = palette.getLightMutedOrPrimary()
    val isLiked by remember { mutableStateOf(false) }

    IconButton(
        modifier = modifier.simpleShadow(color = paletteColor),
        onClick = { /** TODO: favourite database */ }
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(if (isLiked) R.drawable.like_filled else R.drawable.like),
            contentDescription = stringResource(R.string.favourites),
            tint = paletteColor
        )
    }
}

@Composable
private fun PlaylistOrDownloadButton(
    palette: Palette?,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject()
) {
    val audioStatus by storageHandler.audioStatusState.collectAsState()

    when (audioStatus) {
        AudioStatus.STREAMING -> DownloadButton(palette, modifier)
        else -> CurrentPlaylistButton(palette, modifier)
    }
}

@Composable
private fun DownloadButton(palette: Palette?, modifier: Modifier = Modifier) {
    val paletteColor = palette.getLightMutedOrPrimary()
    val isCashPropertiesDialogShownState = remember { mutableStateOf(false) }

    Box(modifier) {
        val (areStoragePermissionsGranted, launchStoragePermissions) = externalStoragePermissionsRequestLauncher(
            isCashPropertiesDialogShownState,
            modifier = Modifier.align(Alignment.Center)
        )

        IconButton(
            modifier = modifier.simpleShadow(color = paletteColor),
            onClick = {
                if (!areStoragePermissionsGranted) {
                    launchStoragePermissions()
                    isCashPropertiesDialogShownState.value = true
                }
            }
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                painter = painterResource(R.drawable.save_icon),
                contentDescription = stringResource(R.string.download_as_mp3),
                tint = paletteColor
            )
        }

        if (isCashPropertiesDialogShownState.value && areStoragePermissionsGranted)
            CashPropertiesDialog(
                isDialogShownState = isCashPropertiesDialogShownState,
                modifier = Modifier.align(Alignment.Center)
            )
    }
}

@Composable
private fun CurrentPlaylistButton(
    palette: Palette?,
    modifier: Modifier = Modifier,
) {
    val paletteColor = palette.getLightMutedOrPrimary()

    IconButton(
        modifier = modifier.simpleShadow(color = paletteColor),
        onClick = { /** TODO: Current playlist view */ }
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(R.drawable.playlists),
            contentDescription = stringResource(R.string.current_playlist),
            tint = paletteColor
        )
    }
}