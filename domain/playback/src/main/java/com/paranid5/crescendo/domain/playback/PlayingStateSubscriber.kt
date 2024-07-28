package com.paranid5.crescendo.domain.playback

import kotlinx.coroutines.flow.StateFlow

interface PlayingStateSubscriber {
    val isPlayingState: StateFlow<Boolean>
}