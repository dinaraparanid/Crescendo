package com.paranid5.crescendo.system.services.track.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.system.services.track.TrackService
import com.paranid5.crescendo.system.services.track.playback.isRepeating
import kotlinx.coroutines.launch

internal fun RepeatChangedReceiver(service: TrackService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val playerProvider = service.playerProvider

            service.serviceScope.launch {
                playerProvider.setAndStoreRepeating(!playerProvider.isRepeating)
                service.notificationManager.updateNotification()
            }
        }
    }