package com.paranid5.crescendo.system.services.stream.playback

sealed interface PlaybackEvent {
    data class StartSameStream(
        private val id: Long = System.currentTimeMillis()
    ) : PlaybackEvent

    data class StartNewStream(
        val ytUrl: String,
        val initialPosition: Long,
        private val id: Long = System.currentTimeMillis()
    ) : PlaybackEvent

    data class Resume(
        private val id: Long = System.currentTimeMillis()
    ) : PlaybackEvent

    data class Pause(
        private val id: Long = System.currentTimeMillis()
    ) : PlaybackEvent

    data class SeekTo(
        val position: Long,
        private val id: Long = System.currentTimeMillis()
    ) : PlaybackEvent

    data class SeekTenSecsForward(
        private val id: Long = System.currentTimeMillis()
    ) : PlaybackEvent

    data class SeekTenSecsBack(
        private val id: Long = System.currentTimeMillis()
    ) : PlaybackEvent
}