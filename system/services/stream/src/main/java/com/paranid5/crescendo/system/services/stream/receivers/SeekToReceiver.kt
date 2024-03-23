package com.paranid5.crescendo.system.services.stream.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.system.services.stream.StreamService
import com.paranid5.crescendo.system.services.stream.positionArg
import kotlinx.coroutines.launch

internal fun SeekToReceiver(service: StreamService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val position = intent.positionArg

            service.serviceScope.launch {
                service.playerProvider.seekToViaPlayer(position)
            }
        }
    }