package com.paranid5.crescendo.presentation.main.audio_effects

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.states.effects.AudioEffectsEnabledStateSubscriber
import com.paranid5.crescendo.data.states.effects.AudioEffectsEnabledStateSubscriberImpl
import com.paranid5.crescendo.data.states.effects.BassStrengthStateSubscriber
import com.paranid5.crescendo.data.states.effects.BassStrengthStateSubscriberImpl
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
import com.paranid5.crescendo.data.states.effects.SpeedTextStateSubscriber
import com.paranid5.crescendo.data.states.effects.SpeedTextStateSubscriberImpl

class AudioEffectsViewModel(private val storageHandler: StorageHandler) :
    ViewModel(),
    AudioStatusStateSubscriber by AudioStatusStateSubscriberImpl(storageHandler),
    AudioEffectsEnabledStateSubscriber by AudioEffectsEnabledStateSubscriberImpl(storageHandler),
    BassStrengthStateSubscriber by BassStrengthStateSubscriberImpl(storageHandler),
    ReverbPresetStateSubscriber by ReverbPresetStateSubscriberImpl(storageHandler),
    PitchStatePublisher by PitchStatePublisherImpl(storageHandler),
    SpeedStatePublisher by SpeedStatePublisherImpl(storageHandler),
    PitchTextStateSubscriber by PitchTextStateSubscriberImpl(storageHandler),
    SpeedTextStateSubscriber by SpeedTextStateSubscriberImpl(storageHandler)