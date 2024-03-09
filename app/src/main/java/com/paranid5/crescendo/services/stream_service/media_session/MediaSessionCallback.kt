package com.paranid5.crescendo.services.stream_service.media_session

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import com.paranid5.crescendo.utils.extensions.sendBroadcast
import com.paranid5.crescendo.services.stream_service.StreamService
import com.paranid5.crescendo.services.stream_service.playback.pauseAsync
import com.paranid5.crescendo.services.stream_service.playback.resumeAsync
import com.paranid5.crescendo.services.stream_service.playback.seekTenSecsBackAsync
import com.paranid5.crescendo.services.stream_service.playback.seekTenSecsForwardAsync
import com.paranid5.crescendo.services.stream_service.playback.seekToAsync

fun MediaSessionCallback(service: StreamService) =
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
            service.sendBroadcast(service.commandsToActions[action]!!.playbackAction)
        }
    }