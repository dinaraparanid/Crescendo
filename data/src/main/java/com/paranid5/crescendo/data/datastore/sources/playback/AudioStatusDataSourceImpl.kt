package com.paranid5.crescendo.data.datastore.sources.playback

import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.audioStatusFlow
import com.paranid5.crescendo.data.properties.storeAudioStatus
import com.paranid5.crescendo.domain.sources.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.sources.playback.AudioStatusSubscriber

class AudioStatusSubscriberImpl(private val dataStoreProvider: DataStoreProvider) :
    AudioStatusSubscriber {
    override val audioStatusFlow by lazy {
        dataStoreProvider.audioStatusFlow
    }
}

class AudioStatusPublisherImpl(private val dataStoreProvider: DataStoreProvider) :
    AudioStatusPublisher {
    override suspend fun setAudioStatus(audioStatus: AudioStatus) =
        dataStoreProvider.storeAudioStatus(audioStatus)
}