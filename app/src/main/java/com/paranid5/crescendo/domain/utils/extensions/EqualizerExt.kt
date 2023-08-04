package com.paranid5.crescendo.domain.utils.extensions

import android.media.audiofx.Equalizer
import com.paranid5.crescendo.data.eq.EqualizerData
import com.paranid5.crescendo.data.eq.EqualizerParameters

inline val Equalizer.bandIndicesRange
    get() = (0 until numberOfBands)

inline var Equalizer.bandLevels
    get() = bandIndicesRange.map { getBandLevel(it.toShort()) }
    set(value) = value.forEachIndexed { ind, level -> setBandLevel(ind.toShort(), level) }

inline var Equalizer.preset
    get() = currentPreset
    set(value) = usePreset(if (value == EqualizerData.NO_EQ_PRESET) 0 else value)

inline val Equalizer.presets: List<String>
    get() = (0 until numberOfPresets).map { getPresetName(it.toShort()) }

inline val Equalizer.frequencies
    get() = bandIndicesRange.map { getCenterFreq(it.toShort()) }

fun Equalizer.setParameter(
    currentParameter: EqualizerParameters,
    bandLevels: List<Short>?,
    preset: Short
) = when (currentParameter) {
    EqualizerParameters.BANDS -> this.bandLevels = bandLevels!!
    EqualizerParameters.PRESET -> this.preset = preset
    EqualizerParameters.NIL -> Unit
}