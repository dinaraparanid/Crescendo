package com.paranid5.crescendo.services.stream_service.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.services.stream_service.StreamService2
import kotlinx.coroutines.launch

fun ResumeReceiver(service: StreamService2) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            service.serviceScope.launch {
                service.playerProvider.resume()
            }
        }
    }