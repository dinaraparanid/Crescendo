package com.paranid5.crescendo.core.media.eq

import android.media.audiofx.Equalizer
import com.paranid5.crescendo.core.common.eq.EqualizerBandsPreset
import com.paranid5.crescendo.core.common.eq.EqualizerData
import kotlinx.collections.immutable.toImmutableList

object EqualizerData {
    fun fromEqualizer(
        eq: Equalizer,
        bandLevels: List<Short>?,
        currentPreset: Short,
        currentParameter: EqualizerBandsPreset
    ) = EqualizerData(
        minBandLevel = eq.bandLevelRange[0],
        maxBandLevel = eq.bandLevelRange[1],
        bandLevels = when (currentParameter) {
            EqualizerBandsPreset.CUSTOM -> bandLevels!!
            else -> eq.bandLevels
        }.toImmutableList(),
        presets = eq.presets.toImmutableList(),
        currentPreset = when (currentParameter) {
            EqualizerBandsPreset.CUSTOM -> EqualizerData.NO_EQ_PRESET
            else -> currentPreset
        },
        bandFrequencies = eq.frequencies.toImmutableList(),
        bandsPreset = currentParameter
    )
}