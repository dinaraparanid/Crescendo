package com.paranid5.crescendo.domain.playback

import kotlinx.coroutines.flow.Flow

interface RepeatingSubscriber {
    val isRepeatingFlow: Flow<Boolean>
}