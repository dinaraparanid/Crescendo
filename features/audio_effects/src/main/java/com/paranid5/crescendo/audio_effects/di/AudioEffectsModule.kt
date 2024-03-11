package com.paranid5.crescendo.audio_effects.di

import com.paranid5.crescendo.audio_effects.domain.AudioEffectsInteractor
import com.paranid5.crescendo.audio_effects.presentation.AudioEffectsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val audioEffectsModule = module {
    singleOf(::AudioEffectsInteractor)
    viewModelOf(::AudioEffectsViewModel)
}