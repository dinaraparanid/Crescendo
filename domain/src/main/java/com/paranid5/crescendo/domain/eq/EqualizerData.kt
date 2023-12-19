package com.paranid5.crescendo.domain.eq

import android.media.audiofx.Equalizer
import com.paranid5.crescendo.domain.utils.extensions.bandLevels
import com.paranid5.crescendo.domain.utils.extensions.frequencies
import com.paranid5.crescendo.domain.utils.extensions.presets

data class EqualizerData(
    val minBandLevel: Short,
    val maxBandLevel: Short,
    val bandLevels: List<Short>,
    val presets: List<String>,
    val currentPreset: Short,
    val bandFrequencies: List<Int>,
    val bandsPreset: EqualizerBandsPreset,
) {
    companion object {
        const val NO_EQ_PRESET: Short = -1
    }

    constructor(
        eq: Equalizer,
        bandLevels: List<Short>?,
        currentPreset: Short,
        currentParameter: EqualizerBandsPreset
    ) : this(
        minBandLevel = eq.bandLevelRange[0],
        maxBandLevel = eq.bandLevelRange[1],
        bandLevels = when (currentParameter) {
            EqualizerBandsPreset.CUSTOM -> bandLevels!!
            else -> eq.bandLevels
        },
        presets = eq.presets,
        currentPreset = when (currentParameter) {
            EqualizerBandsPreset.CUSTOM -> NO_EQ_PRESET
            else -> currentPreset
        },
        bandFrequencies = eq.frequencies,
        bandsPreset = currentParameter
    )
}
