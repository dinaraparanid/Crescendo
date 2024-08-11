package com.paranid5.crescendo.domain.audio_effects

import kotlinx.coroutines.flow.Flow

interface SpeedTextDataSource {
    val speedTextFlow: Flow<String>
}
