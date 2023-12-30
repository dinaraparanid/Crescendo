package com.paranid5.crescendo.data.states.tracks

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.currentPlaylistFlow
import com.paranid5.crescendo.data.properties.storeCurrentPlaylist
import com.paranid5.crescendo.domain.tracks.DefaultTrack
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface CurrentPlaylistStateSubscriber {
    val currentPlaylistFlow: Flow<ImmutableList<DefaultTrack>>
}

interface CurrentPlaylistStatePublisher {
    suspend fun setCurrentPlaylist(playlist: ImmutableList<DefaultTrack>)
}

class CurrentPlaylistStateSubscriberImpl(private val storageHandler: StorageHandler) :
    CurrentPlaylistStateSubscriber {
    override val currentPlaylistFlow by lazy {
        storageHandler.currentPlaylistFlow
    }
}

class CurrentPlaylistStatePublisherImpl(private val storageHandler: StorageHandler) :
    CurrentPlaylistStatePublisher {
    override suspend fun setCurrentPlaylist(playlist: ImmutableList<DefaultTrack>) =
        storageHandler.storeCurrentPlaylist(playlist)
}