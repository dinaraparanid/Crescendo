package com.paranid5.crescendo.data.states.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.pitchTextFlow
import kotlinx.coroutines.flow.Flow

interface PitchTextStateSubscriber {
    val pitchTextFlow: Flow<String>
}

class PitchTextStateSubscriberImpl(private val storageRepository: StorageRepository) :
    PitchTextStateSubscriber {
    override val pitchTextFlow by lazy {
        storageRepository.pitchTextFlow
    }
}