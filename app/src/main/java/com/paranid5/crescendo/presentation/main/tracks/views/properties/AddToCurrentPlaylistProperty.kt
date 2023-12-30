package com.paranid5.crescendo.presentation.main.tracks.views.properties

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.R
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.currentPlaylistFlow
import com.paranid5.crescendo.data.properties.storeCurrentPlaylist
import com.paranid5.crescendo.domain.tracks.DefaultTrack
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState
import com.paranid5.crescendo.services.track_service.TrackServiceAccessor
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Composable
fun AddToCurrentPlaylistProperty(
    track: Track,
    storageHandler: StorageHandler,
    trackServiceAccessor: TrackServiceAccessor,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    val currentPlaylist by storageHandler
        .currentPlaylistFlow
        .collectLatestAsState(initial = persistentListOf())

    DropdownMenuItem(
        modifier = modifier,
        text = { Text(stringResource(R.string.add_to_cur_playlist)) },
        onClick = {
            coroutineScope.launch {
                val defaultTrack = DefaultTrack(track)
                trackServiceAccessor.addToPlaylist(defaultTrack)

                storageHandler.storeCurrentPlaylist(
                    (currentPlaylist + defaultTrack).toImmutableList()
                )
            }
        }
    )
}