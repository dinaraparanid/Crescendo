package com.paranid5.crescendo.core.media.extensions

import android.media.audiofx.Equalizer
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerBandsPreset
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerData
import kotlinx.collections.immutable.toImmutableList

fun EqualizerData.Companion.fromEqualizer(
    eq: Equalizer,
    bandLevels: List<Short>?,
    currentPreset: Short,
    currentParameter: EqualizerBandsPreset,
) = EqualizerData(
    minBandLevel = eq.bandLevelRange[0],
    maxBandLevel = eq.bandLevelRange[1],
    bandLevels = when (currentParameter) {
        EqualizerBandsPreset.CUSTOM -> bandLevels!!
        else -> eq.bandLevels
    }.toImmutableList(),
    presets = eq.presets,
    currentPreset = when (currentParameter) {
        EqualizerBandsPreset.CUSTOM -> NO_EQ_PRESET
        else -> currentPreset
    },
    bandFrequencies = eq.frequencies,
    bandsPreset = currentParameter,
)