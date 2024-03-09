package com.paranid5.crescendo.core.media.eq

import android.media.audiofx.PresetReverb

object PresetReverb {
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

    inline val presetsNumber
        get() = presets.size
}