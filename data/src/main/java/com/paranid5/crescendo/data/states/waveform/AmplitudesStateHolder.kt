package com.paranid5.crescendo.data.states.waveform

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.amplitudesFlow
import com.paranid5.crescendo.data.properties.storeAmplitudes
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface AmplitudesStateSubscriber {
    val amplitudesFlow: Flow<ImmutableList<Int>>
}

interface AmplitudesStatePublisher {
    suspend fun setAmplitudes(amplitudes: ImmutableList<Int>)
}

class AmplitudesStateSubscriberImpl(private val storageHandler: StorageHandler) :
    AmplitudesStateSubscriber {
    override val amplitudesFlow by lazy {
        storageHandler.amplitudesFlow
    }
}

class AmplitudesStatePublisherImpl(private val storageHandler: StorageHandler) :
    AmplitudesStatePublisher {
    override suspend fun setAmplitudes(amplitudes: ImmutableList<Int>) =
        storageHandler.storeAmplitudes(amplitudes)
}