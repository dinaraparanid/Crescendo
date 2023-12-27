package com.paranid5.crescendo.presentation.main.current_playlist.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.paranid5.crescendo.domain.tracks.DefaultTrack
import com.paranid5.crescendo.presentation.main.current_playlist.CurrentPlaylistViewModel
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState
import com.paranid5.crescendo.services.track_service.TrackServiceAccessor
import kotlinx.collections.immutable.ImmutableList
import org.koin.compose.koinInject

@Composable
fun TrackDismissEffect(
    viewModel: CurrentPlaylistViewModel,
    trackPathDismissKey: String,
    playlistDismissMediator: ImmutableList<DefaultTrack>,
    trackIndexDismissMediator: Int,
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) {
    val currentTrackIndex by viewModel
        .currentTrackIndexFlow
        .collectLatestAsState(initial = 0)

    LaunchedEffect(trackPathDismissKey) {
        if (trackPathDismissKey.isNotEmpty()) {
            viewModel.storeCurrentPlaylist(playlistDismissMediator)

            if (trackIndexDismissMediator < currentTrackIndex)
                viewModel.storeCurrentTrackIndex(currentTrackIndex - 1)

            trackServiceAccessor.removeFromPlaylist(trackIndexDismissMediator)
        }
    }
}