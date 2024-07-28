package com.paranid5.crescendo.audio_effects.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.datastore.sources.playback.AudioStatusSubscriberImpl
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsRepository
import com.paranid5.crescendo.domain.sources.playback.AudioStatusSubscriber

class AudioEffectsViewModel(
    dataStoreProvider: DataStoreProvider,
    audioEffectsRepository: AudioEffectsRepository,
) : ViewModel(),
    AudioStatusSubscriber by AudioStatusSubscriberImpl(dataStoreProvider),
    AudioEffectsRepository by audioEffectsRepository