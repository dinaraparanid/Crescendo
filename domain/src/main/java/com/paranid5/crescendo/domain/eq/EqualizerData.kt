package com.paranid5.crescendo.domain.eq

import android.media.audiofx.Equalizer
import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.domain.utils.extensions.bandLevels
import com.paranid5.crescendo.domain.utils.extensions.frequencies
import com.paranid5.crescendo.domain.utils.extensions.presets
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Immutable
data class EqualizerData(
    val minBandLevel: Short,
    val maxBandLevel: Short,
    val bandLevels: ImmutableList<Short>,
    val presets: ImmutableList<String>,
    val currentPreset: Short,
    val bandFrequencies: ImmutableList<Int>,
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
        }.toImmutableList(),
        presets = eq.presets.toImmutableList(),
        currentPreset = when (currentParameter) {
            EqualizerBandsPreset.CUSTOM -> NO_EQ_PRESET
            else -> currentPreset
        },
        bandFrequencies = eq.frequencies.toImmutableList(),
        bandsPreset = currentParameter
    )
}
