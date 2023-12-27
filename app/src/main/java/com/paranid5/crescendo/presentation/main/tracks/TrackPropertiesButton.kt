package com.paranid5.crescendo.presentation.main.tracks

import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.R
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.storeCurrentPlaylist
import com.paranid5.crescendo.domain.tracks.DefaultTrack
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.presentation.main.Screens
import com.paranid5.crescendo.presentation.composition_locals.LocalActivity
import com.paranid5.crescendo.presentation.composition_locals.LocalNavController
import com.paranid5.crescendo.presentation.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.setTrack
import com.paranid5.crescendo.services.track_service.TrackServiceAccessor
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun TrackPropertiesButton(
    track: Track,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
    tint: Color = Color.White,
    storageHandler: StorageHandler = koinInject(),
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) {
    val isPropertiesMenuShown = remember { mutableStateOf(false) }

    Box(modifier) {
        TrackPropertiesMenu(
            track = track,
            isPropertiesMenuShown = isPropertiesMenuShown,
            itemModifier = itemModifier,
            storageHandler = storageHandler,
            trackServiceAccessor = trackServiceAccessor,
        )

        IconButton(
            modifier = modifier,
            onClick = { isPropertiesMenuShown.value = true }
        ) {
            Icon(
                modifier = iconModifier,
                painter = painterResource(R.drawable.three_dots),
                contentDescription = stringResource(R.string.settings),
                tint = tint
            )
        }
    }
}

@Composable
private fun TrackPropertiesMenu(
    track: Track,
    isPropertiesMenuShown: MutableState<Boolean>,
    storageHandler: StorageHandler,
    trackServiceAccessor: TrackServiceAccessor,
    modifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = isPropertiesMenuShown.value,
        onDismissRequest = { isPropertiesMenuShown.value = false },
        modifier = modifier
    ) {
        AddToCurrentPlaylistItem(
            track = track,
            modifier = itemModifier,
            storageHandler = storageHandler,
            trackServiceAccessor = trackServiceAccessor
        )

        TrimTrackItem(
            track = track,
            modifier = itemModifier
        )
    }
}

@Composable
private fun AddToCurrentPlaylistItem(
    track: Track,
    storageHandler: StorageHandler,
    trackServiceAccessor: TrackServiceAccessor,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    DropdownMenuItem(
        modifier = modifier,
        text = { Text(stringResource(R.string.add_to_cur_playlist)) },
        onClick = {
            scope.launch {
                val defaultTrack = DefaultTrack(track)
                trackServiceAccessor.addToPlaylist(defaultTrack)

                val currentPlaylist = storageHandler.currentPlaylistState.value
                storageHandler.storeCurrentPlaylist((currentPlaylist + defaultTrack).toImmutableList())
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TrimTrackItem(
    track: Track,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!
    val navController = LocalNavController.current
    val playingSheetState = LocalPlayingSheetState.current

    val trimmerViewModel = koinViewModel<TrimmerViewModel>(viewModelStoreOwner = activity)
    val coroutineScope = rememberCoroutineScope()

    DropdownMenuItem(
        modifier = modifier,
        text = { Text(stringResource(R.string.trim_track)) },
        onClick = {
            trimmerViewModel.setTrack(track)
            navController.navigateIfNotSame(Screens.Audio.Trimmer)
            coroutineScope.launch { playingSheetState?.bottomSheetState?.collapse() }
        }
    )
}