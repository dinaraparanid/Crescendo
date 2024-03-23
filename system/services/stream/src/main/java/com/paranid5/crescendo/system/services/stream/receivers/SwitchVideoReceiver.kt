package com.paranid5.crescendo.system.services.stream.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.system.services.stream.StreamService
import com.paranid5.crescendo.system.services.stream.playback.startStreamAsync
import com.paranid5.crescendo.system.services.stream.urlArg

internal fun SwitchVideoReceiver(service: StreamService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val url = intent.urlArg
            service.startStreamAsync(url)
        }
    }