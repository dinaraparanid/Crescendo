package com.paranid5.crescendo.ui.track.item.properties

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.system.services.track.TrackServiceInteractor
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun AddToCurrentPlaylistProperty(
    track: TrackUiState,
    modifier: Modifier = Modifier,
    currentPlaylistRepository: CurrentPlaylistRepository = koinInject(),
    trackServiceAccessor: TrackServiceInteractor = koinInject(),
) {
    val coroutineScope = rememberCoroutineScope()

    DropdownMenuItem(
        modifier = modifier,
        leadingIcon = { PropertyIcon(ImageVector.vectorResource(R.drawable.ic_playlist)) },
        text = { PropertyText(stringResource(R.string.track_kebab_add_to_cur_playlist)) },
        onClick = {
            coroutineScope.launch {
                val defaultTrack = DefaultTrack(track)
                trackServiceAccessor.addToPlaylist(defaultTrack)
                currentPlaylistRepository.addTrackToPlaylist(defaultTrack)
            }
        }
    )
}
