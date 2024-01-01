package com.paranid5.crescendo.services.core.notification

import android.app.Service
import android.os.Build

@Suppress("DEPRECATION")
fun Service.detachNotification() = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->
        stopForeground(Service.STOP_FOREGROUND_REMOVE)

    else -> stopForeground(true)
}