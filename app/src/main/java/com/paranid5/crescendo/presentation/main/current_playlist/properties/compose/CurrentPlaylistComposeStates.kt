package com.paranid5.crescendo.presentation.main.current_playlist.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.domain.tracks.DefaultTrack
import com.paranid5.crescendo.presentation.main.current_playlist.CurrentPlaylistViewModel
import com.paranid5.crescendo.presentation.main.current_playlist.properties.currentPlaylistFlow
import com.paranid5.crescendo.presentation.main.current_playlist.properties.currentTrackIndexFlow
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun CurrentPlaylistViewModel.collectCurrentPlaylistAsState(
    initial: ImmutableList<DefaultTrack> = persistentListOf()
) = currentPlaylistFlow.collectLatestAsState(initial)

@Composable
fun CurrentPlaylistViewModel.collectCurrentTrackIndexAsState(initial: Int = 0) =
    currentTrackIndexFlow.collectLatestAsState(initial)