package com.paranid5.crescendo.domain.sources.tracks

import kotlinx.coroutines.flow.Flow

interface CurrentTrackIndexSubscriber {
    val currentTrackIndexFlow: Flow<Int>
}

interface CurrentTrackIndexPublisher {
    suspend fun setCurrentTrackIndex(index: Int)
}