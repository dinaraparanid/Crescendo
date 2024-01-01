package com.paranid5.crescendo.services.core.receivers

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.services.core.notification.detachNotification

fun DismissNotificationReceiver(service: Service) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) =
            service.detachNotification()
    }