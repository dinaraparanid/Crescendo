package com.paranid5.crescendo.current_playlist.presentation.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.current_playlist.presentation.CurrentPlaylistViewModel
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
internal fun CurrentPlaylistViewModel.collectPlaylistDismissMediatorAsState() =
    playlistDismissMediatorState.collectLatestAsState()

@Composable
internal fun CurrentPlaylistViewModel.collectTrackIndexDismissMediatorAsState() =
    trackIndexDismissMediatorState.collectLatestAsState()

@Composable
internal fun CurrentPlaylistViewModel.collectTrackPathDismissKeyAsState() =
    trackPathDismissKeyState.collectLatestAsState()