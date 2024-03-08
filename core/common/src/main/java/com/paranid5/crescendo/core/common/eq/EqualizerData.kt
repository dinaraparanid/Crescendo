package com.paranid5.crescendo.core.common.eq

import kotlinx.collections.immutable.ImmutableList

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
}
