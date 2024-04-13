package com.paranid5.crescendo.data.sources.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.pitchTextFlow
import com.paranid5.crescendo.domain.sources.effects.PitchTextSubscriber

class PitchTextSubscriberImpl(private val storageRepository: StorageRepository) :
    PitchTextSubscriber {
    override val pitchTextFlow by lazy {
        storageRepository.pitchTextFlow
    }
}