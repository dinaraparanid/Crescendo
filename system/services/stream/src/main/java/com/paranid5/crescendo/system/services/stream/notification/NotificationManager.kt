package com.paranid5.crescendo.system.services.stream.notification

import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.paranid5.crescendo.domain.stream.CurrentMetadataSubscriber
import com.paranid5.crescendo.domain.stream.StreamRepository
import com.paranid5.crescendo.system.services.stream.StreamService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

internal const val STREAM_NOTIFICATION_ID = 101
internal const val STREAM_CHANNEL_ID = "stream_channel"

internal class NotificationManager(
    service: StreamService,
    streamRepository: StreamRepository,
) : CurrentMetadataSubscriber by streamRepository {
    internal val currentMetadataState by lazy {
        currentMetadataFlow.stateIn(
            scope = service.serviceScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
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
    fun invalidateNotification() = playerNotificationManager.invalidate()

    @OptIn(UnstableApi::class)
    fun releasePlayer() = playerNotificationManager.setPlayer(null)
}
