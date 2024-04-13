package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.data.StorageRepository
import kotlinx.collections.immutable.ImmutableList

val StorageRepository.amplitudesFlow
    get() = waveformStateDataSource.amplitudesFlow

suspend fun StorageRepository.storeAmplitudes(amplitudes: ImmutableList<Int>) =
    waveformStateDataSource.storeAmplitudes(amplitudes)