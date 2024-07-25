package com.paranid5.crescendo.domain.audio_effects

import kotlinx.coroutines.flow.Flow

interface PitchTextDataSource {
    val pitchTextFlow: Flow<String>
}