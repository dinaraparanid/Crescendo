package com.paranid5.crescendo.system.services.stream.media_session

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import com.paranid5.crescendo.system.services.stream.StreamService
import com.paranid5.crescendo.system.services.stream.playback.pauseAsync
import com.paranid5.crescendo.system.services.stream.playback.resumeAsync
import com.paranid5.crescendo.system.services.stream.playback.seekTenSecsBackAsync
import com.paranid5.crescendo.system.services.stream.playback.seekTenSecsForwardAsync
import com.paranid5.crescendo.system.services.stream.playback.seekToAsync
import com.paranid5.crescendo.utils.extensions.sendAppBroadcast

internal fun MediaSessionCallback(service: StreamService) =
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
            service.seekTenSecsForwardAsync()
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            service.seekTenSecsBackAsync()
        }

        override fun onCustomAction(action: String, extras: Bundle?) {
            super.onCustomAction(action, extras)
            service.sendAppBroadcast(service.commandsToActions[action]!!.playbackAction)
        }
    }