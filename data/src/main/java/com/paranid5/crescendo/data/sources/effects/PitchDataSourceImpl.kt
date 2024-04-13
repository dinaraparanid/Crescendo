package com.paranid5.crescendo.data.sources.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.pitchFlow
import com.paranid5.crescendo.data.properties.storePitch
import com.paranid5.crescendo.domain.sources.effects.PitchPublisher
import com.paranid5.crescendo.domain.sources.effects.PitchSubscriber

class PitchSubscriberImpl(private val storageRepository: StorageRepository) : PitchSubscriber {
    override val pitchFlow by lazy {
        storageRepository.pitchFlow
    }
}

class PitchPublisherImpl(private val storageRepository: StorageRepository) : PitchPublisher {
    override suspend fun setPitch(pitch: Float) =
        storageRepository.storePitch(pitch)
}