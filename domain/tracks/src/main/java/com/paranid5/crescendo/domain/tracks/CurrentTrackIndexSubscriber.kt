package com.paranid5.crescendo.domain.tracks

import kotlinx.coroutines.flow.Flow

interface CurrentTrackIndexSubscriber {
    val currentTrackIndexFlow: Flow<Int>
}
