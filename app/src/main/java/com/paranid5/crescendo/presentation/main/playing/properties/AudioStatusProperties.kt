package com.paranid5.crescendo.presentation.main.playing.properties

import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.presentation.main.playing.PlayingViewModel

inline val PlayingViewModel.audioStatusFlow
    get() = audioStatusStateHolder.audioStatusFlow

suspend inline fun PlayingViewModel.setAudioStatus(audioStatus: AudioStatus) =
    audioStatusStateHolder.setAudioStatus(audioStatus)