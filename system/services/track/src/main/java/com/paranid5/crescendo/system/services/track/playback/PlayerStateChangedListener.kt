package com.paranid5.crescendo.system.services.track.playback

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import com.paranid5.crescendo.system.services.track.TrackService
import com.paranid5.crescendo.system.services.track.showErrNotificationAndSendBroadcast

internal fun PlayerStateChangedListener(service: TrackService) =
    object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
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
            service.showErrNotificationAndSendBroadcast(error)
        }
    }
