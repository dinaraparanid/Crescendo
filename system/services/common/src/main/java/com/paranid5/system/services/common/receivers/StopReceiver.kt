package com.paranid5.system.services.common.receivers

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.system.services.common.ConnectionManager
import timber.log.Timber

private const val TAG = "StopReceiver"

fun <S> StopReceiver(service: S) where S : Service, S : ConnectionManager =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber
                .tag(TAG)
                .d("Stopped after stop receive: ${service.stopSelfResult(service.startId)}")
        }
    }
