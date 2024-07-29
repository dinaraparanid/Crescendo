package com.paranid5.crescendo.domain.playback

interface RepeatingPublisher {
    suspend fun updateRepeating(isRepeating: Boolean)
}