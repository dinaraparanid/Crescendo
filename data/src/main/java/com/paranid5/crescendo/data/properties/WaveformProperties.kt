package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import kotlinx.collections.immutable.ImmutableList

val DataStoreProvider.amplitudesFlow
    get() = waveformStateDataSource.amplitudesFlow

suspend fun DataStoreProvider.storeAmplitudes(amplitudes: ImmutableList<Int>) =
    waveformStateDataSource.storeAmplitudes(amplitudes)