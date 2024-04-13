package com.paranid5.crescendo.current_playlist.presentation.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.paranid5.crescendo.current_playlist.presentation.CurrentPlaylistViewModel
import com.paranid5.crescendo.current_playlist.presentation.properties.compose.collectCurrentTrackIndexAsState
import com.paranid5.crescendo.current_playlist.presentation.properties.compose.collectPlaylistDismissMediatorAsState
import com.paranid5.crescendo.current_playlist.presentation.properties.compose.collectTrackIndexDismissMediatorAsState
import com.paranid5.crescendo.current_playlist.presentation.properties.compose.collectTrackPathDismissKeyAsState
import com.paranid5.crescendo.system.services.track.TrackServiceAccessor
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal fun TrackDismissEffect(
    viewModel: CurrentPlaylistViewModel = koinViewModel(),
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) {
    val currentTrackIndex by viewModel.collectCurrentTrackIndexAsState()
    val playlistDismissMediator by viewModel.collectPlaylistDismissMediatorAsState()
    val trackIndexDismissMediator by viewModel.collectTrackIndexDismissMediatorAsState()
    val trackPathDismissKey by viewModel.collectTrackPathDismissKeyAsState()

    LaunchedEffect(trackPathDismissKey) {
        if (trackPathDismissKey.isNotEmpty()) {
            viewModel.setCurrentPlaylist(playlistDismissMediator)

            if (trackIndexDismissMediator < currentTrackIndex)
                viewModel.setCurrentTrackIndex(currentTrackIndex - 1)

            trackServiceAccessor.removeFromPlaylist(trackIndexDismissMediator)
        }
    }
}