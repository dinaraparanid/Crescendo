package com.paranid5.crescendo.audio_effects.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsRepository
import com.paranid5.crescendo.domain.playback.AudioStatusSubscriber
import com.paranid5.crescendo.domain.playback.PlaybackRepository

class AudioEffectsViewModel(
    audioEffectsRepository: AudioEffectsRepository,
    playbackRepository: PlaybackRepository,
) : ViewModel(),
    AudioStatusSubscriber by playbackRepository,
    AudioEffectsRepository by audioEffectsRepository