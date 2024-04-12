package com.paranid5.crescendo.data.sources.waveform

import com.paranid5.crescendo.data.StorageRepository
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

class AmplitudesStateSubscriberImpl(private val storageRepository: StorageRepository) :
    AmplitudesStateSubscriber {
    override val amplitudesFlow by lazy {
        storageRepository.amplitudesFlow
    }
}

class AmplitudesStatePublisherImpl(private val storageRepository: StorageRepository) :
    AmplitudesStatePublisher {
    override suspend fun setAmplitudes(amplitudes: ImmutableList<Int>) =
        storageRepository.storeAmplitudes(amplitudes)
}