package com.paranid5.mediastreamer.data.eq

import android.media.audiofx.Equalizer
import com.paranid5.mediastreamer.domain.utils.extensions.bandLevels
import com.paranid5.mediastreamer.domain.utils.extensions.frequencies
import com.paranid5.mediastreamer.domain.utils.extensions.presets

data class EqualizerData(
    val minBandLevel: Short,
    val maxBandLevel: Short,
    val bandLevels: List<Short>,
    val presets: List<String>,
    val currentPreset: Short,
    val bandFrequencies: List<Int>,
    val currentParameter: EqualizerParameters,
) {
    companion object {
        const val NO_EQ_PRESET: Short = -1
    }

    constructor(
        eq: Equalizer,
        bandLevels: List<Short>?,
        currentPreset: Short,
        currentParameter: EqualizerParameters
    ) : this(
        minBandLevel = eq.bandLevelRange[0],
        maxBandLevel = eq.bandLevelRange[1],
        bandLevels = when (currentParameter) {
            EqualizerParameters.BANDS -> bandLevels!!
            else -> eq.bandLevels
        },
        presets = eq.presets,
        currentPreset = when (currentParameter) {
            EqualizerParameters.BANDS -> NO_EQ_PRESET
            else -> currentPreset
        },
        bandFrequencies = eq.frequencies,
        currentParameter = currentParameter
    )
}
