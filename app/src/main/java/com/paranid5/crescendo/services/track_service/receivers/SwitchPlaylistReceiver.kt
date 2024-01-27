package com.paranid5.crescendo.services.track_service.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.services.track_service.TrackService
import com.paranid5.crescendo.services.track_service.TrackServiceStart
import com.paranid5.crescendo.services.track_service.playback.pauseAsync
import com.paranid5.crescendo.services.track_service.playback.playPlaylistAsync
import com.paranid5.crescendo.services.track_service.playback.resumeAsync
import com.paranid5.crescendo.services.track_service.startType

fun SwitchPlaylistReceiver(service: TrackService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val startType = intent.startType

            when {
                startType == TrackServiceStart.NEW_PLAYLIST ->
                    service.playPlaylistAsync()

                startType == TrackServiceStart.RESUME && service.playerProvider.isPlaying ->
                    service.pauseAsync()

                else -> service.resumeAsync()
            }
        }
    }