package com.paranid5.crescendo.services.track_service.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.services.track_service.TrackService
import com.paranid5.crescendo.services.track_service.playback.removeTrackFromPlaylistAsync
import com.paranid5.crescendo.services.track_service.trackIndexArg

fun RemoveTrackReceiver(service: TrackService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val index = intent.trackIndexArg
            service.removeTrackFromPlaylistAsync(index)
        }
    }