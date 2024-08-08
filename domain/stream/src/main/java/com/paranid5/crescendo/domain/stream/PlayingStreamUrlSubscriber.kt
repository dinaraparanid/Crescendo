package com.paranid5.crescendo.domain.stream

import kotlinx.coroutines.flow.Flow

interface PlayingStreamUrlSubscriber {
    val playingUrlFlow: Flow<String>
}
