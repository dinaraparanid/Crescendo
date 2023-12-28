package com.paranid5.crescendo.presentation.main.current_playlist.properties

import com.paranid5.crescendo.domain.tracks.DefaultTrack
import com.paranid5.crescendo.presentation.main.current_playlist.CurrentPlaylistViewModel
import kotlinx.collections.immutable.ImmutableList

inline val CurrentPlaylistViewModel.currentPlaylistFlow
    get() = currentPlaylistStateHolder.currentPlaylistFlow

suspend inline fun CurrentPlaylistViewModel.storeCurrentPlaylist(
    playlist: ImmutableList<DefaultTrack>
) = currentPlaylistStateHolder.storeCurrentPlaylist(playlist)

inline val CurrentPlaylistViewModel.currentTrackIndexFlow
    get() = currentPlaylistStateHolder.currentTrackIndexFlow

suspend inline fun CurrentPlaylistViewModel.storeCurrentTrackIndex(index: Int) =
    currentPlaylistStateHolder.storeCurrentTrackIndex(index)
