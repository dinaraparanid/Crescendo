package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.domain.audio_effects.AudioEffectsEnabledDataSource
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsRepository
import com.paranid5.crescendo.domain.audio_effects.BassStrengthDataSource
import com.paranid5.crescendo.domain.audio_effects.EqualizerBandsDataSource
import com.paranid5.crescendo.domain.audio_effects.EqualizerParamDataSource
import com.paranid5.crescendo.domain.audio_effects.EqualizerPresetDataSource
import com.paranid5.crescendo.domain.audio_effects.EqualizerStateHolder
import com.paranid5.crescendo.domain.audio_effects.PitchDataSource
import com.paranid5.crescendo.domain.audio_effects.PitchTextDataSource
import com.paranid5.crescendo.domain.audio_effects.ReverbPresetDataSource
import com.paranid5.crescendo.domain.audio_effects.SpeedDataSource
import com.paranid5.crescendo.domain.audio_effects.SpeedTextDataSource

internal class AudioEffectsRepositoryImpl(
    audioEffectsEnabledDataSource: AudioEffectsEnabledDataSource,
    bassStrengthDataSource: BassStrengthDataSource,
    equalizerStateHolder: EqualizerStateHolder,
    equalizerBandsDataSource: EqualizerBandsDataSource,
    equalizerParamDataSource: EqualizerParamDataSource,
    equalizerPresetDataSource: EqualizerPresetDataSource,
    pitchDataSource: PitchDataSource,
    pitchTextDataSource: PitchTextDataSource,
    reverbPresetDataSource: ReverbPresetDataSource,
    speedDataSource: SpeedDataSource,
    speedTextDataSource: SpeedTextDataSource,
) : AudioEffectsRepository,
    AudioEffectsEnabledDataSource by audioEffectsEnabledDataSource,
    BassStrengthDataSource by bassStrengthDataSource,
    EqualizerStateHolder by equalizerStateHolder,
    EqualizerBandsDataSource by equalizerBandsDataSource,
    EqualizerParamDataSource by equalizerParamDataSource,
    EqualizerPresetDataSource by equalizerPresetDataSource,
    PitchDataSource by pitchDataSource,
    PitchTextDataSource by pitchTextDataSource,
    ReverbPresetDataSource by reverbPresetDataSource,
    SpeedDataSource by speedDataSource,
    SpeedTextDataSource by speedTextDataSource