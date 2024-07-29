package com.paranid5.crescendo.domain.waveform

interface AmplitudesPublisher {
    suspend fun updateAmplitudes(amplitudes: List<Int>)
}
