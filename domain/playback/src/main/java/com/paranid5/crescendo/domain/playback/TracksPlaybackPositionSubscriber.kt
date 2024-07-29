package com.paranid5.crescendo.domain.playback

import kotlinx.coroutines.flow.Flow

interface TracksPlaybackPositionSubscriber {
    val tracksPlaybackPositionFlow: Flow<Long>
}
