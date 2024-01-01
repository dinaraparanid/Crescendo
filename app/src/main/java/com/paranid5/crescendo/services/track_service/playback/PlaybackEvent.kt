package com.paranid5.crescendo.services.track_service.playback

import com.paranid5.crescendo.domain.tracks.Track

sealed interface PlaybackEvent {
    data class StartSamePlaylist(
        private val id: Long = System.currentTimeMillis()
    ) : PlaybackEvent

    data class StartNewPlaylist(
        val playlist: List<Track>,
        val trackIndex: Int,
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
        val playlist: List<Track>,
        val index: Int,
        private val id: Long = System.currentTimeMillis()
    ) : PlaybackEvent
}