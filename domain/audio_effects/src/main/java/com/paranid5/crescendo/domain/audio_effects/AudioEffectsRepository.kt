package com.paranid5.crescendo.domain.audio_effects

interface AudioEffectsRepository :
    AudioEffectsEnabledDataSource,
    BassStrengthDataSource,
    EqualizerStateHolder,
    EqualizerBandsDataSource,
    EqualizerParamDataSource,
    EqualizerPresetDataSource,
    PitchDataSource,
    PitchTextDataSource,
    ReverbPresetDataSource,
    SpeedDataSource,
    SpeedTextDataSource