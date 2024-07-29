package com.paranid5.crescendo.data.waveform

import com.paranid5.crescendo.data.datastore.WaveformDataStore
import com.paranid5.crescendo.domain.waveform.AmplitudesPublisher

internal class AmplitudesPublisherImpl(
    private val waveformDataStore: WaveformDataStore,
) : AmplitudesPublisher {
    override suspend fun updateAmplitudes(amplitudes: List<Int>) =
        waveformDataStore.storeAmplitudes(amplitudes)
}
