package com.paranid5.crescendo.domain.sources.effects

import kotlinx.coroutines.flow.Flow

interface SpeedTextSubscriber {
    val speedTextState: Flow<String>
}