package com.paranid5.crescendo.presentation.main.tracks.views.properties

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState
import com.paranid5.crescendo.services.track_service.TrackServiceAccessor
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AddToCurrentPlaylistProperty(
    track: com.paranid5.crescendo.core.common.tracks.Track,
    modifier: Modifier = Modifier,
    currentPlaylistRepository: CurrentPlaylistRepository = koinInject(),
    trackServiceAccessor: TrackServiceAccessor = koinInject(),
) {
    val coroutineScope = rememberCoroutineScope()

    val currentPlaylist by currentPlaylistRepository
        .tracksFlow
        .collectLatestAsState(initial = persistentListOf())

    DropdownMenuItem(
        modifier = modifier,
        text = { Text(stringResource(R.string.add_to_cur_playlist)) },
        onClick = {
            coroutineScope.launch {
                val defaultTrack = com.paranid5.crescendo.core.common.tracks.DefaultTrack(track)
                trackServiceAccessor.addToPlaylist(defaultTrack)

                currentPlaylistRepository.replacePlaylistAsync(
                    (currentPlaylist + defaultTrack).toImmutableList()
                ).join()
            }
        }
    )
}