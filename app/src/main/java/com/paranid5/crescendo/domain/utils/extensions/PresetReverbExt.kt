package com.paranid5.crescendo.domain.utils.extensions

import android.media.audiofx.PresetReverb

object PresetReverbExt {
    inline val presets
        get() = listOf(
            PresetReverb.PRESET_NONE,
            PresetReverb.PRESET_SMALLROOM,
            PresetReverb.PRESET_MEDIUMROOM,
            PresetReverb.PRESET_LARGEROOM,
            PresetReverb.PRESET_MEDIUMHALL,
            PresetReverb.PRESET_LARGEHALL,
            PresetReverb.PRESET_PLATE,
        )
}