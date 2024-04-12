package com.paranid5.crescendo.data.sources.tracks

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.currentTrackIndexFlow
import com.paranid5.crescendo.data.properties.storeCurrentTrackIndex
import kotlinx.coroutines.flow.Flow

interface CurrentTrackIndexStateSubscriber {
    val currentTrackIndexFlow: Flow<Int>
}

interface CurrentTrackIndexStatePublisher {
    suspend fun setCurrentTrackIndex(index: Int)
}

class CurrentTrackIndexStateSubscriberImpl(private val storageRepository: StorageRepository) :
    CurrentTrackIndexStateSubscriber {
    override val currentTrackIndexFlow by lazy {
        storageRepository.currentTrackIndexFlow
    }
}

class CurrentTrackIndexStatePublisherImpl(private val storageRepository: StorageRepository) :
    CurrentTrackIndexStatePublisher {
    override suspend fun setCurrentTrackIndex(index: Int) =
        storageRepository.storeCurrentTrackIndex(index)
}