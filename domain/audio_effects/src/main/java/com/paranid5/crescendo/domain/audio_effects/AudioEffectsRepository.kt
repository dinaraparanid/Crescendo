package com.paranid5.crescendo.domain.audio_effects

interface AudioEffectsRepository :
    AudioEffectsEnabledDataSource,
    BassStrengthDataSource,
    EqualizerBandsDataSource,
    EqualizerParamDataSource,
    EqualizerPresetDataSource,
    PitchDataSource,
    PitchTextDataSource,
    ReverbPresetDataSource,
    SpeedDataSource,
    SpeedTextDataSource