package com.paranid5.crescendo.domain.tracks

interface CurrentTrackIndexPublisher {
    suspend fun updateCurrentTrackIndex(index: Int)
}
