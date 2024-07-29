package com.paranid5.crescendo.data.waveform

import com.paranid5.crescendo.domain.waveform.AmplitudesPublisher
import com.paranid5.crescendo.domain.waveform.AmplitudesSubscriber
import com.paranid5.crescendo.domain.waveform.WaveformRepository

internal class WaveformRepositoryImpl(
    amplitudesSubscriber: AmplitudesSubscriber,
    amplitudesPublisher: AmplitudesPublisher,
) : WaveformRepository,
    AmplitudesSubscriber by amplitudesSubscriber,
    AmplitudesPublisher by amplitudesPublisher
