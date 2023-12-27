package com.paranid5.crescendo.presentation.main.current_playlist

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.currentPlaylistFlow
import com.paranid5.crescendo.data.properties.currentTrackIndexFlow
import com.paranid5.crescendo.data.properties.storeCurrentPlaylist
import com.paranid5.crescendo.data.properties.storeCurrentTrackIndex
import com.paranid5.crescendo.domain.tracks.DefaultTrack
import kotlinx.collections.immutable.ImmutableList

class CurrentPlaylistViewModel(private val storageHandler: StorageHandler) : ViewModel() {
    val currentPlaylistFlow by lazy {
        storageHandler.currentPlaylistFlow
    }

    val currentTrackIndexFlow by lazy {
        storageHandler.currentTrackIndexFlow
    }

    suspend fun storeCurrentPlaylist(playlist: ImmutableList<DefaultTrack>) =
        storageHandler.storeCurrentPlaylist(playlist)

    suspend fun storeCurrentTrackIndex(index: Int) =
        storageHandler.storeCurrentTrackIndex(index)
}