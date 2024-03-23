package com.paranid5.crescendo.audio_effects.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.states.effects.AudioEffectsEnabledStatePublisher
import com.paranid5.crescendo.data.states.effects.AudioEffectsEnabledStatePublisherImpl
import com.paranid5.crescendo.data.states.effects.AudioEffectsEnabledStateSubscriber
import com.paranid5.crescendo.data.states.effects.AudioEffectsEnabledStateSubscriberImpl
import com.paranid5.crescendo.data.states.effects.BassStrengthStatePublisher
import com.paranid5.crescendo.data.states.effects.BassStrengthStatePublisherImpl
import com.paranid5.crescendo.data.states.effects.BassStrengthStateSubscriber
import com.paranid5.crescendo.data.states.effects.BassStrengthStateSubscriberImpl
import com.paranid5.crescendo.data.states.effects.EqualizerBandsStatePublisher
import com.paranid5.crescendo.data.states.effects.EqualizerBandsStatePublisherImpl
import com.paranid5.crescendo.data.states.effects.EqualizerParamStatePublisher
import com.paranid5.crescendo.data.states.effects.EqualizerParamStatePublisherImpl
import com.paranid5.crescendo.data.states.effects.EqualizerPresetStatePublisher
import com.paranid5.crescendo.data.states.effects.EqualizerPresetStatePublisherImpl
import com.paranid5.crescendo.data.states.effects.PitchStatePublisher
import com.paranid5.crescendo.data.states.effects.PitchStatePublisherImpl
import com.paranid5.crescendo.data.states.effects.ReverbPresetStateSubscriber
import com.paranid5.crescendo.data.states.effects.ReverbPresetStateSubscriberImpl
import com.paranid5.crescendo.data.states.effects.SpeedStatePublisher
import com.paranid5.crescendo.data.states.effects.SpeedStatePublisherImpl
import com.paranid5.crescendo.data.states.playback.AudioStatusStateSubscriber
import com.paranid5.crescendo.data.states.playback.AudioStatusStateSubscriberImpl
import com.paranid5.crescendo.data.states.effects.PitchTextStateSubscriber
import com.paranid5.crescendo.data.states.effects.PitchTextStateSubscriberImpl
import com.paranid5.crescendo.data.states.effects.ReverbPresetStatePublisher
import com.paranid5.crescendo.data.states.effects.ReverbPresetStatePublisherImpl
import com.paranid5.crescendo.data.states.effects.SpeedTextStateSubscriber
import com.paranid5.crescendo.data.states.effects.SpeedTextStateSubscriberImpl

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