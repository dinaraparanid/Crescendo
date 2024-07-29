package com.paranid5.crescendo.domain.stream

import kotlinx.coroutines.flow.Flow

interface PlayingUrlSubscriber {
    val playingUrlFlow: Flow<String>
}
