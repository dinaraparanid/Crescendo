package com.paranid5.crescendo.system.services.stream.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.system.services.stream.StreamService
import com.paranid5.crescendo.system.services.stream.playback.isRepeating
import kotlinx.coroutines.launch

internal fun RepeatChangedReceiver(service: StreamService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val playerProvider = service.playerProvider

            service.serviceScope.launch {
                playerProvider.setAndStoreRepeating(!playerProvider.isRepeating)
                service.notificationManager.invalidateNotification()
            }
        }
    }