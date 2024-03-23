package com.paranid5.crescendo.system.services.track.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.system.services.track.TrackService
import com.paranid5.crescendo.system.services.track.playback.replacePlaylistAsync

internal fun PlaylistDraggedReceiver(service: TrackService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            service.replacePlaylistAsync()
        }
    }