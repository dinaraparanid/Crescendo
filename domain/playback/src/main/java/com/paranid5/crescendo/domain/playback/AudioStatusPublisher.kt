package com.paranid5.crescendo.domain.playback

import com.paranid5.crescendo.core.common.AudioStatus

interface AudioStatusPublisher {
    suspend fun updateAudioStatus(audioStatus: AudioStatus)
}