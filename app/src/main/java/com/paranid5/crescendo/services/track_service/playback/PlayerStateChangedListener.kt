package com.paranid5.crescendo.services.track_service.playback

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import com.paranid5.crescendo.services.track_service.TrackService
import com.paranid5.crescendo.services.track_service.sendErrorBroadcast

fun PlayerStateChangedListener(service: TrackService) =
    object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)

            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO)
                service.updateCurrentTrackIndexAsync()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)

            if (playbackState == Player.STATE_IDLE)
                service.restartPlayerAsync()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            service.playerProvider.isPlaying = isPlaying

            when {
                isPlaying -> service.startPlaybackPositionMonitoringAsync()
                else -> stopPlaybackPositionMonitoring()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            service.playerProvider.isStoppedWithError = true
            service.sendErrorBroadcast(error)
        }
    }