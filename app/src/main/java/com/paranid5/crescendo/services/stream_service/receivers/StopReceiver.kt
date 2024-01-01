package com.paranid5.crescendo.services.stream_service.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.paranid5.crescendo.services.stream_service.StreamService2

private const val TAG = "StreamService"

fun StopReceiver(service: StreamService2) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Stopped after stop receive: ${service.stopSelfResult(service.startId)}")
        }
    }