package com.paranid5.crescendo.presentation.main.current_playlist.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.current_playlist.CurrentPlaylistViewModel
import com.paranid5.crescendo.presentation.main.current_playlist.properties.compose.collectCurrentTrackIndexAsState
import com.paranid5.crescendo.presentation.main.current_playlist.properties.compose.collectPlaylistDismissMediatorAsState
import com.paranid5.crescendo.presentation.main.current_playlist.properties.compose.collectTrackIndexDismissMediatorAsState
import com.paranid5.crescendo.presentation.main.current_playlist.properties.compose.collectTrackPathDismissKeyAsState
import com.paranid5.crescendo.services.track_service.TrackServiceAccessor
import org.koin.compose.koinInject

@Composable
fun TrackDismissEffect(
    viewModel: CurrentPlaylistViewModel = koinActivityViewModel(),
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