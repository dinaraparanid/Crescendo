package com.paranid5.crescendo.data.sources.tracks

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.currentTrackIndexFlow
import com.paranid5.crescendo.data.properties.storeCurrentTrackIndex
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackIndexPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackIndexSubscriber

class CurrentTrackIndexSubscriberImpl(private val storageRepository: StorageRepository) :
    CurrentTrackIndexSubscriber {
    override val currentTrackIndexFlow by lazy {
        storageRepository.currentTrackIndexFlow
    }
}

class CurrentTrackIndexPublisherImpl(private val storageRepository: StorageRepository) :
    CurrentTrackIndexPublisher {
    override suspend fun setCurrentTrackIndex(index: Int) =
        storageRepository.storeCurrentTrackIndex(index)
}