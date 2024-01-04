package com.paranid5.crescendo.presentation.main.audio_effects

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageHandler
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

class AudioEffectsViewModel(storageHandler: StorageHandler) : ViewModel(),
    AudioStatusStateSubscriber by AudioStatusStateSubscriberImpl(storageHandler),
    AudioEffectsEnabledStateSubscriber by AudioEffectsEnabledStateSubscriberImpl(storageHandler),
    AudioEffectsEnabledStatePublisher by AudioEffectsEnabledStatePublisherImpl(storageHandler),
    EqualizerParamStatePublisher by EqualizerParamStatePublisherImpl(storageHandler),
    EqualizerPresetStatePublisher by EqualizerPresetStatePublisherImpl(storageHandler),
    EqualizerBandsStatePublisher by EqualizerBandsStatePublisherImpl(storageHandler),
    BassStrengthStateSubscriber by BassStrengthStateSubscriberImpl(storageHandler),
    BassStrengthStatePublisher by BassStrengthStatePublisherImpl(storageHandler),
    ReverbPresetStateSubscriber by ReverbPresetStateSubscriberImpl(storageHandler),
    ReverbPresetStatePublisher by ReverbPresetStatePublisherImpl(storageHandler),
    PitchStatePublisher by PitchStatePublisherImpl(storageHandler),
    SpeedStatePublisher by SpeedStatePublisherImpl(storageHandler),
    PitchTextStateSubscriber by PitchTextStateSubscriberImpl(storageHandler),
    SpeedTextStateSubscriber by SpeedTextStateSubscriberImpl(storageHandler)