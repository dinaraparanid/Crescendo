package com.paranid5.crescendo.domain.sources.waveform

import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface AmplitudesSubscriber {
    val amplitudesFlow: Flow<ImmutableList<Int>>
}

interface AmplitudesPublisher {
    suspend fun setAmplitudes(amplitudes: ImmutableList<Int>)
}