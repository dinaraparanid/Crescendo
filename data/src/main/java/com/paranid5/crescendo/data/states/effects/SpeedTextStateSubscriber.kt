package com.paranid5.crescendo.data.states.effects

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.speedTextFlow
import kotlinx.coroutines.flow.Flow

interface SpeedTextStateSubscriber {
    val speedTextState: Flow<String>
}

class SpeedTextStateSubscriberImpl(private val storageHandler: StorageHandler) :
    SpeedTextStateSubscriber {
    override val speedTextState by lazy {
        storageHandler.speedTextFlow
    }
}