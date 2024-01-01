package com.paranid5.crescendo.services.stream_service.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.services.stream_service.StreamService
import com.paranid5.crescendo.services.stream_service.playback.isRepeating
import kotlinx.coroutines.launch

fun RepeatChangedReceiver(service: StreamService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val playerProvider = service.playerProvider

            service.serviceScope.launch {
                playerProvider.setAndStoreRepeating(!playerProvider.isRepeating)
                service.notificationManager.updateNotification()
            }
        }
    }