package com.paranid5.crescendo.data.states.tracks

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.currentPlaylistFlow
import com.paranid5.crescendo.data.properties.storeCurrentPlaylist
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.domain.utils.extensions.timeString
import com.paranid5.crescendo.domain.utils.extensions.totalDurationMillis
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CurrentPlaylistStateSubscriber {
    val currentPlaylistFlow: Flow<ImmutableList<Track>>
}

interface CurrentPlaylistStatePublisher {
    suspend fun setCurrentPlaylist(playlist: List<Track>)
}

class CurrentPlaylistStateSubscriberImpl(private val storageHandler: StorageHandler) :
    CurrentPlaylistStateSubscriber {
    override val currentPlaylistFlow by lazy {
        storageHandler.currentPlaylistFlow
    }
}

class CurrentPlaylistStatePublisherImpl(private val storageHandler: StorageHandler) :
    CurrentPlaylistStatePublisher {
    override suspend fun setCurrentPlaylist(playlist: List<Track>) =
        storageHandler.storeCurrentPlaylist(playlist)
}

inline val CurrentPlaylistStateSubscriber.currentPlaylistSizeFlow
    get() = currentPlaylistFlow.map { it.size }

inline val CurrentPlaylistStateSubscriber.currentPlaylistDurationMillisFlow
    get() = currentPlaylistFlow.map { it.totalDurationMillis }

inline val CurrentPlaylistStateSubscriber.currentPlaylistDurationStrFlow
    get() = currentPlaylistDurationMillisFlow.map { it.timeString }