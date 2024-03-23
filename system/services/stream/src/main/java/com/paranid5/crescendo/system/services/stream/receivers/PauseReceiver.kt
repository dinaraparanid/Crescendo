package com.paranid5.crescendo.system.services.stream.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.system.services.stream.StreamService
import com.paranid5.crescendo.system.services.stream.playback.pauseAsync

internal fun PauseReceiver(service: StreamService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            service.pauseAsync()
        }
    }