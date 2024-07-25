package com.paranid5.crescendo.audio_effects.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.playback.AudioStatusSubscriberImpl
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsRepository
import com.paranid5.crescendo.domain.sources.playback.AudioStatusSubscriber

class AudioEffectsViewModel(
    storageRepository: StorageRepository,
    audioEffectsRepository: AudioEffectsRepository,
) : ViewModel(),
    AudioStatusSubscriber by AudioStatusSubscriberImpl(storageRepository),
    AudioEffectsRepository by audioEffectsRepository