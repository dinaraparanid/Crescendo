package com.paranid5.crescendo.domain.sources.playback

import com.paranid5.crescendo.core.common.AudioStatus
import kotlinx.coroutines.flow.Flow

interface AudioStatusSubscriber {
    val audioStatusFlow: Flow<AudioStatus?>
}

interface AudioStatusPublisher {
    suspend fun setAudioStatus(audioStatus: AudioStatus)
}