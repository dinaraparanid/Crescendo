package com.paranid5.crescendo.domain.sources.effects

import kotlinx.coroutines.flow.Flow

interface PitchTextSubscriber {
    val pitchTextFlow: Flow<String>
}