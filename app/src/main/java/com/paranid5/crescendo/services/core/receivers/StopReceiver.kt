package com.paranid5.crescendo.services.core.receivers

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.paranid5.crescendo.services.ConnectionManager

private const val TAG = "StopReceiver"

fun <S> StopReceiver(service: S) where S : Service, S : ConnectionManager =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Stopped after stop receive: ${service.stopSelfResult(service.startId)}")
        }
    }