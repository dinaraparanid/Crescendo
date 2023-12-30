package com.paranid5.crescendo.presentation.main.current_playlist.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.presentation.main.current_playlist.CurrentPlaylistViewModel
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState

@Composable
fun CurrentPlaylistViewModel.collectPlaylistDismissMediatorAsState() =
    playlistDismissMediatorState.collectLatestAsState()

@Composable
fun CurrentPlaylistViewModel.collectTrackIndexDismissMediatorAsState() =
    trackIndexDismissMediatorState.collectLatestAsState()

@Composable
fun CurrentPlaylistViewModel.collectTrackPathDismissKeyAsState() =
    trackPathDismissKeyState.collectLatestAsState()