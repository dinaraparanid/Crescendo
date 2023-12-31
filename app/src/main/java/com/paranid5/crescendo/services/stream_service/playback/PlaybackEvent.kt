package com.paranid5.crescendo.services.stream_service.playback

sealed interface PlaybackEvent {
    data object StartSameStream : PlaybackEvent

    data class StartNewStream(val ytUrl: String, val initialPosition: Long) : PlaybackEvent

    data object Resume : PlaybackEvent

    data object Pause : PlaybackEvent

    @JvmInline
    value class SeekTo(val position: Long) : PlaybackEvent

    data object SeekTenSecsForward : PlaybackEvent

    data object SeekTenSecsBack : PlaybackEvent
}