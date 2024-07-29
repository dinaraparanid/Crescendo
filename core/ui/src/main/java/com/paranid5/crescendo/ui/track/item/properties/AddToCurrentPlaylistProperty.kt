package com.paranid5.crescendo.ui.track.item.properties

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.system.services.track.TrackServiceInteractor
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun AddToCurrentPlaylistProperty(
    track: Track,
    modifier: Modifier = Modifier,
    currentPlaylistRepository: CurrentPlaylistRepository = koinInject(),
    trackServiceAccessor: TrackServiceInteractor = koinInject(),
) {
    val coroutineScope = rememberCoroutineScope()

    val currentPlaylist by currentPlaylistRepository
        .currentPlaylistFlow
        .collectLatestAsState(initial = persistentListOf())

    DropdownMenuItem(
        modifier = modifier,
        text = { Text(stringResource(R.string.add_to_cur_playlist)) },
        onClick = {
            coroutineScope.launch {
                val defaultTrack = DefaultTrack(track)
                trackServiceAccessor.addToPlaylist(defaultTrack)
                currentPlaylistRepository.updateCurrentPlaylist((currentPlaylist + defaultTrack))
            }
        }
    )
}
