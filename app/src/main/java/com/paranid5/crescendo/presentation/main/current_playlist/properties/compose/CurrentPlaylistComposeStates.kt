package com.paranid5.crescendo.presentation.main.current_playlist.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.data.states.tracks.currentPlaylistDurationStrFlow
import com.paranid5.crescendo.data.states.tracks.currentPlaylistSizeFlow
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.presentation.main.current_playlist.CurrentPlaylistViewModel
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun CurrentPlaylistViewModel.collectCurrentPlaylistAsState(
    initial: ImmutableList<com.paranid5.crescendo.core.common.tracks.DefaultTrack> = persistentListOf()
) = currentPlaylistFlow.collectLatestAsState(initial)

@Composable
fun CurrentPlaylistViewModel.collectCurrentTrackIndexAsState(initial: Int = 0) =
    currentTrackIndexFlow.collectLatestAsState(initial)

@Composable
fun CurrentPlaylistViewModel.collectCurrentPlaylistSizeAsState(initial: Int = 0) =
    currentPlaylistSizeFlow.collectLatestAsState(initial)

@Composable
fun CurrentPlaylistViewModel.collectCurrentPlaylistDurationStrAsState(initial: String = "") =
    currentPlaylistDurationStrFlow.collectLatestAsState(initial)