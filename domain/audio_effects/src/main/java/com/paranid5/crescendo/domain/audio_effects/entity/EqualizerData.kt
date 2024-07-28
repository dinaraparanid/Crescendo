package com.paranid5.crescendo.domain.audio_effects.entity

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
        const val INITIAL_EQ_PRESET: Short = 0
        const val MILLIBELS_IN_DECIBEL = 1000
    }
}
