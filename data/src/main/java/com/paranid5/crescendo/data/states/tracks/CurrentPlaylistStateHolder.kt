package com.paranid5.crescendo.data.states.tracks

import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.utils.extensions.timeString
import com.paranid5.crescendo.utils.extensions.totalDurationMillis
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CurrentPlaylistStateSubscriber {
    val currentPlaylistFlow: Flow<ImmutableList<Track>>
}

interface CurrentPlaylistStatePublisher {
    suspend fun setCurrentPlaylist(playlist: List<Track>)
}

class CurrentPlaylistStateSubscriberImpl(
    private val currentPlaylistRepository: CurrentPlaylistRepository
) : CurrentPlaylistStateSubscriber {
    override val currentPlaylistFlow by lazy {
        currentPlaylistRepository.tracksFlow
    }
}

class CurrentPlaylistStatePublisherImpl(
    private val currentPlaylistRepository: CurrentPlaylistRepository
) : CurrentPlaylistStatePublisher {
    override suspend fun setCurrentPlaylist(playlist: List<Track>) =
        currentPlaylistRepository.replacePlaylistAsync(playlist).join()
}

inline val CurrentPlaylistStateSubscriber.currentPlaylistSizeFlow
    get() = currentPlaylistFlow.map { it.size }

inline val CurrentPlaylistStateSubscriber.currentPlaylistDurationMillisFlow
    get() = currentPlaylistFlow.map { it.totalDurationMillis }

inline val CurrentPlaylistStateSubscriber.currentPlaylistDurationStrFlow
    get() = currentPlaylistDurationMillisFlow.map { it.timeString }