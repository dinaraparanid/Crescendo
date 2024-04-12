package com.paranid5.crescendo.audio_effects.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.effects.AudioEffectsEnabledStatePublisher
import com.paranid5.crescendo.data.sources.effects.AudioEffectsEnabledStatePublisherImpl
import com.paranid5.crescendo.data.sources.effects.AudioEffectsEnabledStateSubscriber
import com.paranid5.crescendo.data.sources.effects.AudioEffectsEnabledStateSubscriberImpl
import com.paranid5.crescendo.data.sources.effects.BassStrengthStatePublisher
import com.paranid5.crescendo.data.sources.effects.BassStrengthStatePublisherImpl
import com.paranid5.crescendo.data.sources.effects.BassStrengthStateSubscriber
import com.paranid5.crescendo.data.sources.effects.BassStrengthStateSubscriberImpl
import com.paranid5.crescendo.data.sources.effects.EqualizerBandsStatePublisher
import com.paranid5.crescendo.data.sources.effects.EqualizerBandsStatePublisherImpl
import com.paranid5.crescendo.data.sources.effects.EqualizerParamStatePublisher
import com.paranid5.crescendo.data.sources.effects.EqualizerParamStatePublisherImpl
import com.paranid5.crescendo.data.sources.effects.EqualizerPresetStatePublisher
import com.paranid5.crescendo.data.sources.effects.EqualizerPresetStatePublisherImpl
import com.paranid5.crescendo.data.sources.effects.PitchStatePublisher
import com.paranid5.crescendo.data.sources.effects.PitchStatePublisherImpl
import com.paranid5.crescendo.data.sources.effects.ReverbPresetStateSubscriber
import com.paranid5.crescendo.data.sources.effects.ReverbPresetStateSubscriberImpl
import com.paranid5.crescendo.data.sources.effects.SpeedStatePublisher
import com.paranid5.crescendo.data.sources.effects.SpeedStatePublisherImpl
import com.paranid5.crescendo.data.sources.playback.AudioStatusStateSubscriber
import com.paranid5.crescendo.data.sources.playback.AudioStatusStateSubscriberImpl
import com.paranid5.crescendo.data.sources.effects.PitchTextStateSubscriber
import com.paranid5.crescendo.data.sources.effects.PitchTextStateSubscriberImpl
import com.paranid5.crescendo.data.sources.effects.ReverbPresetStatePublisher
import com.paranid5.crescendo.data.sources.effects.ReverbPresetStatePublisherImpl
import com.paranid5.crescendo.data.sources.effects.SpeedTextStateSubscriber
import com.paranid5.crescendo.data.sources.effects.SpeedTextStateSubscriberImpl

class AudioEffectsViewModel(storageRepository: StorageRepository) : ViewModel(),
    AudioStatusStateSubscriber by AudioStatusStateSubscriberImpl(storageRepository),
    AudioEffectsEnabledStateSubscriber by AudioEffectsEnabledStateSubscriberImpl(storageRepository),
    AudioEffectsEnabledStatePublisher by AudioEffectsEnabledStatePublisherImpl(storageRepository),
    EqualizerParamStatePublisher by EqualizerParamStatePublisherImpl(storageRepository),
    EqualizerPresetStatePublisher by EqualizerPresetStatePublisherImpl(storageRepository),
    EqualizerBandsStatePublisher by EqualizerBandsStatePublisherImpl(storageRepository),
    BassStrengthStateSubscriber by BassStrengthStateSubscriberImpl(storageRepository),
    BassStrengthStatePublisher by BassStrengthStatePublisherImpl(storageRepository),
    ReverbPresetStateSubscriber by ReverbPresetStateSubscriberImpl(storageRepository),
    ReverbPresetStatePublisher by ReverbPresetStatePublisherImpl(storageRepository),
    PitchStatePublisher by PitchStatePublisherImpl(storageRepository),
    SpeedStatePublisher by SpeedStatePublisherImpl(storageRepository),
    PitchTextStateSubscriber by PitchTextStateSubscriberImpl(storageRepository),
    SpeedTextStateSubscriber by SpeedTextStateSubscriberImpl(storageRepository)