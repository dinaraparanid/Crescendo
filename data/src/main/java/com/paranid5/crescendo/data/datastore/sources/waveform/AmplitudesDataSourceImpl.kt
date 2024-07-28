package com.paranid5.crescendo.data.datastore.sources.waveform

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.amplitudesFlow
import com.paranid5.crescendo.data.properties.storeAmplitudes
import com.paranid5.crescendo.domain.sources.waveform.AmplitudesPublisher
import com.paranid5.crescendo.domain.sources.waveform.AmplitudesSubscriber
import kotlinx.collections.immutable.ImmutableList

class AmplitudesSubscriberImpl(private val dataStoreProvider: DataStoreProvider) :
    AmplitudesSubscriber {
    override val amplitudesFlow by lazy {
        dataStoreProvider.amplitudesFlow
    }
}

class AmplitudesPublisherImpl(private val dataStoreProvider: DataStoreProvider) :
    AmplitudesPublisher {
    override suspend fun setAmplitudes(amplitudes: ImmutableList<Int>) =
        dataStoreProvider.storeAmplitudes(amplitudes)
}