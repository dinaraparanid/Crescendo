package com.paranid5.crescendo.data.audio_effects.di

import com.paranid5.crescendo.data.audio_effects.AudioEffectsEnabledDataSourceImpl
import com.paranid5.crescendo.data.audio_effects.AudioEffectsRepositoryImpl
import com.paranid5.crescendo.data.audio_effects.BassStrengthDataSourceImpl
import com.paranid5.crescendo.data.audio_effects.EqualizerBandsDataSourceImpl
import com.paranid5.crescendo.data.audio_effects.EqualizerParamDataSourceImpl
import com.paranid5.crescendo.data.audio_effects.EqualizerPresetDataSourceImpl
import com.paranid5.crescendo.data.audio_effects.PitchDataSourceImpl
import com.paranid5.crescendo.data.audio_effects.PitchTextDataSourceImpl
import com.paranid5.crescendo.data.audio_effects.ReverbPresetDataSourceImpl
import com.paranid5.crescendo.data.audio_effects.SpeedDataSourceImpl
import com.paranid5.crescendo.data.audio_effects.SpeedTextDataSourceImpl
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsEnabledDataSource
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsRepository
import com.paranid5.crescendo.domain.audio_effects.BassStrengthDataSource
import com.paranid5.crescendo.domain.audio_effects.EqualizerBandsDataSource
import com.paranid5.crescendo.domain.audio_effects.EqualizerParamDataSource
import com.paranid5.crescendo.domain.audio_effects.EqualizerPresetDataSource
import com.paranid5.crescendo.domain.audio_effects.PitchDataSource
import com.paranid5.crescendo.domain.audio_effects.PitchTextDataSource
import com.paranid5.crescendo.domain.audio_effects.ReverbPresetDataSource
import com.paranid5.crescendo.domain.audio_effects.SpeedDataSource
import com.paranid5.crescendo.domain.audio_effects.SpeedTextDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val audioEffectsModule = module {
    singleOf(::AudioEffectsEnabledDataSourceImpl) bind AudioEffectsEnabledDataSource::class
    singleOf(::BassStrengthDataSourceImpl) bind BassStrengthDataSource::class
    singleOf(::EqualizerBandsDataSourceImpl) bind EqualizerBandsDataSource::class
    singleOf(::EqualizerParamDataSourceImpl) bind EqualizerParamDataSource::class
    singleOf(::EqualizerPresetDataSourceImpl) bind EqualizerPresetDataSource::class
    singleOf(::PitchDataSourceImpl) bind PitchDataSource::class
    singleOf(::PitchTextDataSourceImpl) bind PitchTextDataSource::class
    singleOf(::ReverbPresetDataSourceImpl) bind ReverbPresetDataSource::class
    singleOf(::SpeedDataSourceImpl) bind SpeedDataSource::class
    singleOf(::SpeedTextDataSourceImpl) bind SpeedTextDataSource::class
    singleOf(::AudioEffectsRepositoryImpl) bind AudioEffectsRepository::class
}
