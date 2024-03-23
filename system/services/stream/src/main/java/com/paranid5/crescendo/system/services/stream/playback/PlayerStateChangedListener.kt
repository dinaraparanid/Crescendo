package com.paranid5.crescendo.system.services.stream.playback

import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import com.paranid5.crescendo.system.services.stream.StreamService
import com.paranid5.crescendo.system.services.stream.showErrNotificationAndSendBroadcast

internal fun PlayerStateChangedListener(service: StreamService) =
    object : Player.Listener {
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