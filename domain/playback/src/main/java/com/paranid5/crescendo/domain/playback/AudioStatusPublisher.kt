package com.paranid5.crescendo.domain.playback

import com.paranid5.crescendo.core.common.PlaybackStatus

interface AudioStatusPublisher {
    suspend fun updateAudioStatus(playbackStatus: PlaybackStatus)
}