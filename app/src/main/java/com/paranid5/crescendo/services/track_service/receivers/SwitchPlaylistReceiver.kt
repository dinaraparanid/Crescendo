package com.paranid5.crescendo.services.track_service.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.services.track_service.TrackService
import com.paranid5.crescendo.services.track_service.playback.playPlaylistAsync
import com.paranid5.crescendo.services.track_service.playlistArg
import com.paranid5.crescendo.services.track_service.trackIndexArg

fun SwitchPlaylistReceiver(service: TrackService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val playlist = intent.playlistArg
            val trackInd = intent.trackIndexArg
            service.playPlaylistAsync(playlist, trackInd)
        }
    }