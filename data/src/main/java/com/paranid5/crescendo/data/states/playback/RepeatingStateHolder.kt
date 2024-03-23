package com.paranid5.crescendo.data.states.playback

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.isRepeatingFlow
import com.paranid5.crescendo.data.properties.storeRepeating
import kotlinx.coroutines.flow.Flow

interface RepeatingStateSubscriber {
    val isRepeatingFlow: Flow<Boolean>
}

interface RepeatingStatePublisher {
    suspend fun setRepeating(isRepeating: Boolean)
}

class RepeatingStateSubscriberImpl(private val storageRepository: StorageRepository) :
    RepeatingStateSubscriber {
    override val isRepeatingFlow by lazy {
        storageRepository.isRepeatingFlow
    }
}

class RepeatingStatePublisherImpl(private val storageRepository: StorageRepository) :
    RepeatingStatePublisher {
    override suspend fun setRepeating(isRepeating: Boolean) =
        storageRepository.storeRepeating(isRepeating)
}