package com.paranid5.crescendo.system.services.track.playback

import com.paranid5.crescendo.core.common.tracks.Track

internal sealed interface PlaybackEvent {
    data class StartSamePlaylist(
        private val id: Long = System.currentTimeMillis()
    ) : PlaybackEvent

    data class StartNewPlaylist(
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

    data class SeekToNextTrack(
        private val id: Long = System.currentTimeMillis()
    ) : PlaybackEvent

    data class SeekToPrevTrack(
        private val id: Long = System.currentTimeMillis()
    ) : PlaybackEvent

    data class AddTrackToPlaylist(
        val track: Track,
        private val id: Long = System.currentTimeMillis()
    ) : PlaybackEvent

    data class RemoveTrackFromPlaylist(
        val index: Int,
        private val id: Long = System.currentTimeMillis()
    ) : PlaybackEvent

    data class ReplacePlaylist(
        private val id: Long = System.currentTimeMillis()
    ) : PlaybackEvent
}