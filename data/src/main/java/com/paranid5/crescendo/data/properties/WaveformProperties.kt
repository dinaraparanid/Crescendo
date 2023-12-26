package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.data.StorageHandler

inline val StorageHandler.amplitudesFlow
    get() = waveformStateProvider.amplitudesFlow

suspend inline fun StorageHandler.storeAmplitudes(amplitudes: List<Int>) =
    waveformStateProvider.storeAmplitudes(amplitudes)