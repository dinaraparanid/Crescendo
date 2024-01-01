package com.paranid5.crescendo.services.track_service.media_session

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import com.paranid5.crescendo.domain.utils.extensions.sendBroadcast
import com.paranid5.crescendo.services.track_service.TrackService
import com.paranid5.crescendo.services.track_service.playback.pauseAsync
import com.paranid5.crescendo.services.track_service.playback.resumeAsync
import com.paranid5.crescendo.services.track_service.playback.seekToAsync
import com.paranid5.crescendo.services.track_service.playback.seekToNextTrackAsync
import com.paranid5.crescendo.services.track_service.playback.seekToPrevTrackAsync

fun MediaSessionCallback(service: TrackService) =
    object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()
            service.resumeAsync()
        }

        override fun onPause() {
            super.onPause()
            service.pauseAsync()
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            service.seekToAsync(pos)
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            service.seekToNextTrackAsync()
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            service.seekToPrevTrackAsync()
        }

        override fun onCustomAction(action: String, extras: Bundle?) {
            super.onCustomAction(action, extras)
            service.sendBroadcast(service.commandsToActions[action]!!.playbackAction)
        }
    }