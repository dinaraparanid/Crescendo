package com.paranid5.crescendo.system.services.track.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.system.services.track.TrackService
import com.paranid5.crescendo.system.services.track.playback.seekToAsync
import com.paranid5.crescendo.system.services.track.positionArg

internal fun SeekToReceiver(service: TrackService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val position = intent.positionArg
            service.seekToAsync(position)
        }
    }