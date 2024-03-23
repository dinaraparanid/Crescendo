package com.paranid5.system.services.common.receivers

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.system.services.common.notification.detachNotification

fun DismissNotificationReceiver(service: Service) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) =
            service.detachNotification()
    }