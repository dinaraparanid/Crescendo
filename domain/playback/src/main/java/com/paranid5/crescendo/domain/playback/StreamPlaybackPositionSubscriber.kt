package com.paranid5.crescendo.domain.playback

import kotlinx.coroutines.flow.Flow

interface StreamPlaybackPositionSubscriber {
    val streamPlaybackPositionFlow: Flow<Long>
}
