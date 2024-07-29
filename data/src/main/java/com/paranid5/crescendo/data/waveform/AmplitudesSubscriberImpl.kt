package com.paranid5.crescendo.data.waveform

import com.paranid5.crescendo.data.datastore.WaveformDataStore
import com.paranid5.crescendo.domain.waveform.AmplitudesSubscriber

internal class AmplitudesSubscriberImpl(
    waveformDataStore: WaveformDataStore
) : AmplitudesSubscriber {
    override val amplitudesFlow by lazy {
        waveformDataStore.amplitudesFlow
    }
}
