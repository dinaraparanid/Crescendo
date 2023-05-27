package com.paranid5.mediastreamer.data.eq

import android.annotation.SuppressLint
import android.media.audiofx.Equalizer
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.domain.utils.extensions.bandLevels
import com.paranid5.mediastreamer.domain.utils.extensions.presets
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class EqualizerData(
    val minBandLevel: Short,
    val maxBandLevel: Short,
    val bandLevels: List<Short>,
    val presets: List<String>,
    val curPreset: Short,
    val paramsState: EqualizerParameters,
) {
    companion object : KoinComponent {
        private val storageHandler by inject<StorageHandler>()
        const val NO_EQ_PRESET: Short = -1
    }

    @SuppressLint("SyntheticAccessor")
    constructor(eq: Equalizer) : this(
        minBandLevel = eq.bandLevelRange[0],
        maxBandLevel = eq.bandLevelRange[1],
        bandLevels = storageHandler.equalizerBandsState.value ?: eq.bandLevels,
        presets = eq.presets,
        curPreset = storageHandler.equalizerPresetState.value,
        paramsState = storageHandler.equalizerParamsState.value,
    )
}
