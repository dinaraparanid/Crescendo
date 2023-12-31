package com.paranid5.crescendo.data.states.playback

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.isRepeatingFlow
import com.paranid5.crescendo.data.properties.storeRepeating
import kotlinx.coroutines.flow.Flow

interface RepeatingStateSubscriber {
    val isRepeatingFlow: Flow<Boolean>
}

interface RepeatingStatePublisher {
    suspend fun setRepeating(isRepeating: Boolean)
}

class RepeatingStateSubscriberImpl(private val storageHandler: StorageHandler) :
    RepeatingStateSubscriber {
    override val isRepeatingFlow by lazy {
        storageHandler.isRepeatingFlow
    }
}

class RepeatingStatePublisherImpl(private val storageHandler: StorageHandler) :
    RepeatingStatePublisher {
    override suspend fun setRepeating(isRepeating: Boolean) =
        storageHandler.storeRepeating(isRepeating)
}