package com.paranid5.crescendo.data.sources.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.speedTextFlow
import kotlinx.coroutines.flow.Flow

interface SpeedTextStateSubscriber {
    val speedTextState: Flow<String>
}

class SpeedTextStateSubscriberImpl(private val storageRepository: StorageRepository) :
    SpeedTextStateSubscriber {
    override val speedTextState by lazy {
        storageRepository.speedTextFlow
    }
}