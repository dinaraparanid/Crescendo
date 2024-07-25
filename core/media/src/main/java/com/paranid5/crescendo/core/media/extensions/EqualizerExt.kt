package com.paranid5.crescendo.core.media.extensions

import android.media.audiofx.Equalizer
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerBandsPreset
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerData

inline val Equalizer.bandIndices
    get() = (0 until numberOfBands)

inline var Equalizer.bandLevels
    get() = bandIndices.map { getBandLevel(it.toShort()) }
    set(value) = value.forEachIndexed { ind, level -> setBandLevel(ind.toShort(), level) }

inline var Equalizer.preset
    get() = currentPreset
    set(value) = usePreset(
        when (value) {
            EqualizerData.NO_EQ_PRESET -> EqualizerData.INITIAL_EQ_PRESET
            else -> value
        }
    )

inline val Equalizer.presets: List<String>
    get() = (0 until numberOfPresets).map { getPresetName(it.toShort()) }

inline val Equalizer.frequencies
    get() = bandIndices.map { getCenterFreq(it.toShort()) }

fun Equalizer.usePreset(
    presetType: EqualizerBandsPreset,
    bandLevels: List<Short>?,
    preset: Short
) = when (presetType) {
    EqualizerBandsPreset.CUSTOM -> this.bandLevels = bandLevels!!
    EqualizerBandsPreset.BUILT_IN -> this.preset = preset
    EqualizerBandsPreset.NIL -> Unit
}