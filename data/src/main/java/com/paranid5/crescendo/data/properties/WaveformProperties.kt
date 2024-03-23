package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.data.StorageRepository
import kotlinx.collections.immutable.ImmutableList

inline val StorageRepository.amplitudesFlow
    get() = waveformStateDataSource.amplitudesFlow

suspend inline fun StorageRepository.storeAmplitudes(amplitudes: ImmutableList<Int>) =
    waveformStateDataSource.storeAmplitudes(amplitudes)