package com.paranid5.crescendo.domain.playback

import com.paranid5.crescendo.core.common.PlaybackStatus
import kotlinx.coroutines.flow.Flow

interface AudioStatusSubscriber {
    val playbackStatusFlow: Flow<PlaybackStatus?>
}
