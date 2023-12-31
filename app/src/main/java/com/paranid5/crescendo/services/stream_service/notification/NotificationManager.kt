package com.paranid5.crescendo.services.stream_service.notification

import android.app.Service
import android.os.Build
import androidx.annotation.OptIn
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStateSubscriber
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStateSubscriberImpl
import com.paranid5.crescendo.services.stream_service.StreamService2
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal const val NOTIFICATION_ID = 101
internal const val STREAM_CHANNEL_ID = "stream_channel"

class NotificationManager(service: StreamService2, storageHandler: StorageHandler) :
    CurrentMetadataStateSubscriber by CurrentMetadataStateSubscriberImpl(storageHandler) {
    internal val currentMetadataState by lazy {
        currentMetadataFlow.stateIn(
            service.serviceScope,
            SharingStarted.Lazily,
            null
        )
    }

    @delegate:UnstableApi
    private val playerNotificationManager by lazy {
        PlayerNotificationManager(service)
    }

    @OptIn(UnstableApi::class)
    fun initNotificationManager(player: Player) =
        playerNotificationManager.setPlayer(player)

    @OptIn(UnstableApi::class)
    fun updateNotification() = playerNotificationManager.invalidate()

    @OptIn(UnstableApi::class)
    fun releasePlayer() = playerNotificationManager.setPlayer(null)
}

suspend inline fun StreamService2.startNotificationMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        notificationManager.currentMetadataFlow.collectLatest {
            serviceScope.launch { notificationManager.updateNotification() }
        }
    }

@Suppress("DEPRECATION")
fun StreamService2.detachNotification() = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->
        stopForeground(Service.STOP_FOREGROUND_REMOVE)

    else -> stopForeground(true)
}