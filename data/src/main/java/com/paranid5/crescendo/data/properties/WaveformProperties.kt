package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.data.StorageHandler
import kotlinx.collections.immutable.ImmutableList

inline val StorageHandler.amplitudesFlow
    get() = waveformStateProvider.amplitudesFlow

suspend inline fun StorageHandler.storeAmplitudes(amplitudes: ImmutableList<Int>) =
    waveformStateProvider.storeAmplitudes(amplitudes)