package com.paranid5.crescendo.system.services.track.media_session

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import com.paranid5.crescendo.system.services.track.TrackService
import com.paranid5.crescendo.system.services.track.playback.pauseAsync
import com.paranid5.crescendo.system.services.track.playback.resumeAsync
import com.paranid5.crescendo.system.services.track.playback.seekToAsync
import com.paranid5.crescendo.system.services.track.playback.seekToNextTrackAsync
import com.paranid5.crescendo.system.services.track.playback.seekToPrevTrackAsync
import com.paranid5.crescendo.utils.extensions.sendAppBroadcast

internal fun MediaSessionCallback(service: TrackService) =
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
            service.sendAppBroadcast(service.commandsToActions[action]!!.playbackAction)
        }
    }