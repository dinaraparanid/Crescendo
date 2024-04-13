package com.paranid5.crescendo.current_playlist.presentation.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.current_playlist.presentation.CurrentPlaylistViewModel
import com.paranid5.crescendo.domain.sources.tracks.currentPlaylistDurationStrFlow
import com.paranid5.crescendo.domain.sources.tracks.currentPlaylistSizeFlow
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun CurrentPlaylistViewModel.collectCurrentPlaylistAsState(
    initial: ImmutableList<DefaultTrack> = persistentListOf()
) = currentPlaylistFlow.collectLatestAsState(initial)

@Composable
internal fun CurrentPlaylistViewModel.collectCurrentTrackIndexAsState(initial: Int = 0) =
    currentTrackIndexFlow.collectLatestAsState(initial)

@Composable
internal fun CurrentPlaylistViewModel.collectCurrentPlaylistSizeAsState(initial: Int = 0) =
    currentPlaylistSizeFlow.collectLatestAsState(initial)

@Composable
internal fun CurrentPlaylistViewModel.collectCurrentPlaylistDurationStrAsState(initial: String = "") =
    currentPlaylistDurationStrFlow.collectLatestAsState(initial)