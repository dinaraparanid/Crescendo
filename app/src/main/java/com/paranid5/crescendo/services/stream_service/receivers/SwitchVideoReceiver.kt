package com.paranid5.crescendo.services.stream_service.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.services.stream_service.StreamService
import com.paranid5.crescendo.services.stream_service.playback.storeAndPlayStreamAsync
import com.paranid5.crescendo.services.stream_service.urlArg

fun SwitchVideoReceiver(service: StreamService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val url = intent.urlArg
            service.storeAndPlayStreamAsync(url)
        }
    }