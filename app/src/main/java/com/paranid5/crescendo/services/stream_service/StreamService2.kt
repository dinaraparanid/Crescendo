package com.paranid5.crescendo.services.stream_service

import android.content.Intent
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.services.SuspendService
import com.paranid5.crescendo.services.stream_service.extractor.UrlExtractor
import com.paranid5.crescendo.services.stream_service.managers.ConnectionManager
import com.paranid5.crescendo.services.stream_service.managers.ConnectionManagerImpl
import com.paranid5.crescendo.services.stream_service.notification.NotificationManager
import com.paranid5.crescendo.services.stream_service.managers.ReceiverManager
import com.paranid5.crescendo.services.stream_service.managers.ReceiverManagerImpl
import com.paranid5.crescendo.services.stream_service.managers.connect
import com.paranid5.crescendo.services.stream_service.managers.disconnect
import com.paranid5.crescendo.services.stream_service.notification.startNotificationMonitoring
import com.paranid5.crescendo.services.stream_service.media_session.MediaSessionCallback
import com.paranid5.crescendo.services.stream_service.media_session.MediaSessionManager
import com.paranid5.crescendo.services.stream_service.media_session.startPlaybackStatesMonitoring
import com.paranid5.crescendo.services.stream_service.notification.detachNotification
import com.paranid5.crescendo.services.stream_service.playback.PlayerProvider
import com.paranid5.crescendo.services.stream_service.playback.effects.startBassMonitoring
import com.paranid5.crescendo.services.stream_service.playback.effects.startEqMonitoring
import com.paranid5.crescendo.services.stream_service.playback.effects.startPlaybackEffectsMonitoring
import com.paranid5.crescendo.services.stream_service.playback.effects.startReverbMonitoring
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal const val ACTION_PAUSE = "pause"
internal const val ACTION_RESUME = "resume"
internal const val ACTION_10_SECS_BACK = "back"
internal const val ACTION_10_SECS_FORWARD = "forward"
internal const val ACTION_REPEAT = "repeat"
internal const val ACTION_UNREPEAT = "unrepeat"
internal const val ACTION_DISMISS = "dismiss"

class StreamService2 : SuspendService(), KoinComponent,
    ConnectionManager by ConnectionManagerImpl(),
    ReceiverManager by ReceiverManagerImpl() {
    companion object {
        const val URL_ARG = "url"
    }

    private val storageHandler by inject<StorageHandler>()

    val mediaSessionManager by lazy {
        MediaSessionManager(storageHandler)
    }

    val playerProvider by lazy {
        PlayerProvider(this, storageHandler)
    }

    val urlExtractor by lazy {
        UrlExtractor()
    }

    val notificationManager by lazy {
        NotificationManager(this, storageHandler)
    }

    val commandsToActions = mapOf(
        ACTION_PAUSE to Actions.Pause,
        ACTION_RESUME to Actions.Resume,
        ACTION_10_SECS_BACK to Actions.TenSecsBack,
        ACTION_10_SECS_FORWARD to Actions.TenSecsForward,
        ACTION_REPEAT to Actions.Repeat,
        ACTION_UNREPEAT to Actions.Unrepeat,
        ACTION_DISMISS to Actions.Dismiss
    )

    override fun onCreate() {
        super.onCreate()
        registerReceivers()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        connect(startId)

        mediaSessionManager.initMediaSession(
            service = this@StreamService2,
            mediaSessionCallback = MediaSessionCallback(this@StreamService2),
        )

        notificationManager.initNotificationManager(playerProvider.player)

        launchMonitoringTasks()

        when (val url = intent?.urlArgOrNull) {
            // Continue with previous stream
            null -> serviceScope.launch {
                playerProvider.startResuming()
            }

            // New stream
            else -> serviceScope.launch {
                playerProvider.storeAndPlayStream(url)
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnect()

        detachNotification()
        notificationManager.releasePlayer()

        playerProvider.releasePlayerWithEffects()
        mediaSessionManager.releaseMediaSession()

        unregisterReceivers()
    }
}

private fun StreamService2.launchMonitoringTasks() {
    serviceScope.launch { playerProvider.startPlaybackEventLoop(this@StreamService2) }
    serviceScope.launch { startNotificationMonitoring() }
    serviceScope.launch { startPlaybackStatesMonitoring() }
    serviceScope.launch { startPlaybackEffectsMonitoring() }
    serviceScope.launch { startEqMonitoring() }
    serviceScope.launch { startBassMonitoring() }
    serviceScope.launch { startReverbMonitoring() }
}

private inline val Intent.urlArgOrNull
    get() = getStringExtra(StreamService2.URL_ARG)