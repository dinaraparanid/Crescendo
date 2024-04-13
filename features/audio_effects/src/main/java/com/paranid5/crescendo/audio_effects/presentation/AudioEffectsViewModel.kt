package com.paranid5.crescendo.audio_effects.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.effects.*
import com.paranid5.crescendo.data.sources.playback.AudioStatusSubscriberImpl
import com.paranid5.crescendo.domain.sources.effects.*
import com.paranid5.crescendo.domain.sources.playback.AudioStatusSubscriber

class AudioEffectsViewModel(storageRepository: StorageRepository) : ViewModel(),
    AudioStatusSubscriber by AudioStatusSubscriberImpl(storageRepository),
    AudioEffectsEnabledSubscriber by AudioEffectsEnabledSubscriberImpl(storageRepository),
    AudioEffectsEnabledPublisher by AudioEffectsEnabledPublisherImpl(storageRepository),
    EqualizerParamPublisher by EqualizerParamPublisherImpl(storageRepository),
    EqualizerPresetPublisher by EqualizerPresetStatePublisherImpl(storageRepository),
    EqualizerBandsPublisher by EqualizerBandsPublisherImpl(storageRepository),
    BassStrengthSubscriber by BassStrengthSubscriberImpl(storageRepository),
    BassStrengthPublisher by BassStrengthPublisherImpl(storageRepository),
    ReverbPresetSubscriber by ReverbPresetSubscriberImpl(storageRepository),
    ReverbPresetPublisher by ReverbPresetPublisherImpl(storageRepository),
    PitchPublisher by PitchPublisherImpl(storageRepository),
    SpeedPublisher by SpeedPublisherImpl(storageRepository),
    PitchTextSubscriber by PitchTextSubscriberImpl(storageRepository),
    SpeedTextSubscriber by SpeedTextSubscriberImpl(storageRepository)