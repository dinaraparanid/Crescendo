package com.paranid5.crescendo.domain.playback

import kotlinx.coroutines.flow.StateFlow

interface AudioSessionIdSubscriber {
    val audioSessionIdState: StateFlow<Int>
}