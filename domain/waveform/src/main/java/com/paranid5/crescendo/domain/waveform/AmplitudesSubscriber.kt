package com.paranid5.crescendo.domain.waveform

import kotlinx.coroutines.flow.Flow

interface AmplitudesSubscriber {
    val amplitudesFlow: Flow<List<Int>>
}
