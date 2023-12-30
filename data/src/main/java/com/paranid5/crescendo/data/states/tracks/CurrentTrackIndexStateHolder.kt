package com.paranid5.crescendo.data.states.tracks

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.currentTrackIndexFlow
import com.paranid5.crescendo.data.properties.storeCurrentTrackIndex
import kotlinx.coroutines.flow.Flow

interface CurrentTrackIndexStateSubscriber {
    val currentTrackIndexFlow: Flow<Int>
}

interface CurrentTrackIndexStatePublisher {
    suspend fun setCurrentTrackIndex(index: Int)
}

class CurrentTrackIndexStateSubscriberImpl(private val storageHandler: StorageHandler) :
    CurrentTrackIndexStateSubscriber {
    override val currentTrackIndexFlow by lazy {
        storageHandler.currentTrackIndexFlow
    }
}

class CurrentTrackIndexStatePublisherImpl(private val storageHandler: StorageHandler) :
    CurrentTrackIndexStatePublisher {
    override suspend fun setCurrentTrackIndex(index: Int) =
        storageHandler.storeCurrentTrackIndex(index)
}