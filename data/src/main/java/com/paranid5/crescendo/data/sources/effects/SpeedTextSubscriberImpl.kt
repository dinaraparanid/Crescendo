package com.paranid5.crescendo.data.sources.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.speedTextFlow
import com.paranid5.crescendo.domain.sources.effects.SpeedTextSubscriber

class SpeedTextSubscriberImpl(private val storageRepository: StorageRepository) :
    SpeedTextSubscriber {
    override val speedTextState by lazy {
        storageRepository.speedTextFlow
    }
}