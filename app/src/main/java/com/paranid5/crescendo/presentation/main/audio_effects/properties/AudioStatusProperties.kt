package com.paranid5.crescendo.presentation.main.audio_effects.properties

import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsViewModel

inline val AudioEffectsViewModel.audioStatusFlow
    get() = audioStatusStateHolder.audioStatusFlow