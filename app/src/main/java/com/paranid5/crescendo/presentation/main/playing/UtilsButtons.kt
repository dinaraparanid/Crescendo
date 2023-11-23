package com.paranid5.crescendo.presentation.main.playing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.StorageHandler
import com.paranid5.crescendo.domain.services.stream_service.StreamServiceAccessor
import com.paranid5.crescendo.domain.services.track_service.TrackServiceAccessor
import com.paranid5.crescendo.presentation.composition_locals.LocalCurrentPlaylistSheetState
import com.paranid5.crescendo.presentation.composition_locals.LocalNavController
import com.paranid5.crescendo.presentation.composition_locals.LocalPlayingSheetState
import com.paranid5.crescendo.presentation.main.AudioStatus
import com.paranid5.crescendo.presentation.ui.extensions.getLightMutedOrPrimary
import com.paranid5.crescendo.presentation.ui.extensions.simpleShadow
import com.paranid5.crescendo.presentation.ui.permissions.requests.externalStoragePermissionsRequestLauncher
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun UtilsButtons(
    palette: Palette?,
    audioStatus: AudioStatus?,
    modifier: Modifier = Modifier
) = Row(modifier.fillMaxWidth()) {
    EqualizerButton(
        palette = palette,
        modifier = Modifier.weight(1F)
    )

    RepeatButton(
        palette = palette,
        modifier = Modifier.weight(1F)
    )

    LikeButton(
        palette = palette,
        modifier = Modifier.weight(1F)
    )

    PlaylistOrDownloadButton(
        palette = palette,
        audioStatus = audioStatus,
        modifier = Modifier.weight(1F)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EqualizerButton(
    palette: Palette?,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject()
) {
    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val playingSheetState = LocalPlayingSheetState.current
    val paletteColor = palette.getLightMutedOrPrimary()
    val scope = rememberCoroutineScope()

    Box(modifier) {
        IconButton(
            modifier = Modifier
                .simpleShadow(color = paletteColor)
                .align(Alignment.Center),
            onClick = {
                playingUIHandler.navigateToAudioEffects(context, navHostController)
                scope.launch { playingSheetState?.bottomSheetState?.collapse() }
            }
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                painter = painterResource(R.drawable.equalizer),
                contentDescription = stringResource(R.string.equalizer),
                tint = paletteColor
            )
        }
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

    Box(modifier) {
        IconButton(
            modifier = Modifier
                .simpleShadow(color = paletteColor)
                .align(Alignment.Center),
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
                painter = painterResource(
                    when {
                        isRepeating -> R.drawable.repeat
                        else -> R.drawable.no_repeat
                    }
                ),
                contentDescription = stringResource(R.string.change_repeat),
                tint = paletteColor
            )
        }
    }
}

@Composable
private fun LikeButton(palette: Palette?, modifier: Modifier = Modifier) {
    val paletteColor = palette.getLightMutedOrPrimary()
    val isLiked by remember { mutableStateOf(false) }

    Box(modifier) {
        IconButton(
            modifier = Modifier
                .simpleShadow(color = paletteColor)
                .align(Alignment.Center),
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
}

@Composable
private fun PlaylistOrDownloadButton(
    palette: Palette?,
    audioStatus: AudioStatus?,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject()
) {
    val currentMetadata by storageHandler.currentMetadataState.collectAsState()

    val isLiveStreaming by remember {
        derivedStateOf {
            audioStatus == AudioStatus.STREAMING && currentMetadata?.isLiveStream == true
        }
    }

    when (audioStatus) {
        AudioStatus.STREAMING -> DownloadButton(
            palette,
            isLiveStreaming,
            modifier
        )

        else -> CurrentPlaylistButton(palette, modifier)
    }
}

@Composable
private fun DownloadButton(
    palette: Palette?,
    isLiveStreaming: Boolean,
    modifier: Modifier = Modifier
) {
    val paletteColor = palette.getLightMutedOrPrimary()
    val isCashPropertiesDialogShownState = remember { mutableStateOf(false) }

    Box(modifier) {
        val (areStoragePermissionsGranted, launchStoragePermissions) =
            externalStoragePermissionsRequestLauncher(
                isCashPropertiesDialogShownState,
                modifier = Modifier.align(Alignment.Center)
            )

        IconButton(
            enabled = !isLiveStreaming,
            modifier = Modifier
                .simpleShadow(color = paletteColor)
                .align(Alignment.Center),
            onClick = {
                if (!areStoragePermissionsGranted) {
                    launchStoragePermissions()
                    return@IconButton
                }

                isCashPropertiesDialogShownState.value = true
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
            CachePropertiesDialog(
                isDialogShownState = isCashPropertiesDialogShownState,
                modifier = Modifier.align(Alignment.Center)
            )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CurrentPlaylistButton(
    palette: Palette?,
    modifier: Modifier = Modifier,
) {
    val paletteColor = palette.getLightMutedOrPrimary()
    val scope = rememberCoroutineScope()
    val curPlaylistSheetState = LocalCurrentPlaylistSheetState.current

    Box(modifier) {
        IconButton(
            onClick = { scope.launch { curPlaylistSheetState?.show() } },
            modifier = Modifier
                .simpleShadow(color = paletteColor)
                .align(Alignment.Center),
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                painter = painterResource(R.drawable.playlists),
                contentDescription = stringResource(R.string.current_playlist),
                tint = paletteColor
            )
        }
    }
}