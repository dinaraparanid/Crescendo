package com.paranid5.crescendo.data.sources.waveform

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.amplitudesFlow
import com.paranid5.crescendo.data.properties.storeAmplitudes
import com.paranid5.crescendo.domain.sources.waveform.AmplitudesPublisher
import com.paranid5.crescendo.domain.sources.waveform.AmplitudesSubscriber
import kotlinx.collections.immutable.ImmutableList

class AmplitudesSubscriberImpl(private val storageRepository: StorageRepository) :
    AmplitudesSubscriber {
    override val amplitudesFlow by lazy {
        storageRepository.amplitudesFlow
    }
}

class AmplitudesPublisherImpl(private val storageRepository: StorageRepository) :
    AmplitudesPublisher {
    override suspend fun setAmplitudes(amplitudes: ImmutableList<Int>) =
        storageRepository.storeAmplitudes(amplitudes)
}