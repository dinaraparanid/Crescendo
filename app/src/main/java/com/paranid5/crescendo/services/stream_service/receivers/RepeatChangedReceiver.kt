package com.paranid5.crescendo.services.stream_service.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.media3.common.Player
import com.paranid5.crescendo.services.stream_service.StreamService2
import kotlinx.coroutines.launch

fun RepeatChangedReceiver(service: StreamService2) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val playerProvider = service.playerProvider
            val isRepeating = playerProvider.player.repeatMode == Player.REPEAT_MODE_ONE

            service.serviceScope.launch {
                playerProvider.setAndStoreRepeating(!isRepeating)
                service.notificationManager.updateNotification()
            }
        }
    }