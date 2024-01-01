package com.paranid5.crescendo.services.stream_service.notification

import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStateSubscriber
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStateSubscriberImpl
import com.paranid5.crescendo.services.stream_service.StreamService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

internal const val STREAM_NOTIFICATION_ID = 101
internal const val STREAM_CHANNEL_ID = "stream_channel"

class NotificationManager(service: StreamService, storageHandler: StorageHandler) :
    CurrentMetadataStateSubscriber by CurrentMetadataStateSubscriberImpl(storageHandler) {
    internal val currentMetadataState by lazy {
        currentMetadataFlow.stateIn(
            service.serviceScope,
            SharingStarted.WhileSubscribed(),
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