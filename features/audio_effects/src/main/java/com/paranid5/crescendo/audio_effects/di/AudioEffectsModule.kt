package com.paranid5.crescendo.audio_effects.di

import com.paranid5.crescendo.audio_effects.presentation.AudioEffectsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val audioEffectsModule = module {
    viewModelOf(::AudioEffectsViewModel)
}