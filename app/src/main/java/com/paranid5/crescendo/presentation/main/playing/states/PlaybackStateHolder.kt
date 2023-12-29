package com.paranid5.crescendo.presentation.main.playing.states

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.currentMetadataFlow
import com.paranid5.crescendo.data.properties.currentTrackFlow
import com.paranid5.crescendo.data.properties.isRepeatingFlow
import com.paranid5.crescendo.data.properties.storeStreamPlaybackPosition
import com.paranid5.crescendo.data.properties.storeTracksPlaybackPosition
import com.paranid5.crescendo.data.properties.streamPlaybackPositionFlow
import com.paranid5.crescendo.data.properties.tracksPlaybackPositionFlow

class PlaybackStateHolder(private val storageHandler: StorageHandler) {
    val currentMetadataFlow by lazy {
        storageHandler.currentMetadataFlow
    }

    val currentTrackFlow by lazy {
        storageHandler.currentTrackFlow
    }

    val streamPlaybackPositionFlow by lazy {
        storageHandler.streamPlaybackPositionFlow
    }

    val tracksPlaybackPositionFlow by lazy {
        storageHandler.tracksPlaybackPositionFlow
    }

    val isRepeatingFlow by lazy {
        storageHandler.isRepeatingFlow
    }

    suspend fun setStreamPlaybackPosition(position: Long) =
        storageHandler.storeStreamPlaybackPosition(position)

    suspend fun setTracksPlaybackPosition(position: Long) =
        storageHandler.storeTracksPlaybackPosition(position)
}