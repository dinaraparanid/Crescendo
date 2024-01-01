package com.paranid5.crescendo.services.stream_service

import android.content.Intent
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.services.SuspendService
import com.paranid5.crescendo.services.stream_service.extractor.UrlExtractor
import com.paranid5.crescendo.services.stream_service.notification.NotificationManager
import com.paranid5.crescendo.services.stream_service.notification.startNotificationMonitoring
import com.paranid5.crescendo.services.stream_service.media_session.MediaSessionCallback
import com.paranid5.crescendo.services.stream_service.media_session.MediaSessionManager
import com.paranid5.crescendo.services.stream_service.media_session.startMetadataMonitoring
import com.paranid5.crescendo.services.stream_service.media_session.startPlaybackStatesMonitoring
import com.paranid5.crescendo.services.stream_service.notification.detachNotification
import com.paranid5.crescendo.services.stream_service.playback.PlayerProvider
import com.paranid5.crescendo.services.stream_service.playback.effects.startBassMonitoring
import com.paranid5.crescendo.services.stream_service.playback.effects.startEqMonitoring
import com.paranid5.crescendo.services.stream_service.playback.effects.startPlaybackEffectsMonitoring
import com.paranid5.crescendo.services.stream_service.playback.effects.startReverbMonitoring
import com.paranid5.crescendo.services.stream_service.receivers.DismissNotificationReceiver
import com.paranid5.crescendo.services.stream_service.receivers.PauseReceiver
import com.paranid5.crescendo.services.stream_service.receivers.RepeatChangedReceiver
import com.paranid5.crescendo.services.stream_service.receivers.ResumeReceiver
import com.paranid5.crescendo.services.stream_service.receivers.SeekToReceiver
import com.paranid5.crescendo.services.stream_service.receivers.StopReceiver
import com.paranid5.crescendo.services.stream_service.receivers.SwitchVideoReceiver
import com.paranid5.crescendo.services.stream_service.receivers.TenSecsBackReceiver
import com.paranid5.crescendo.services.stream_service.receivers.TenSecsForwardReceiver
import com.paranid5.crescendo.services.stream_service.receivers.registerReceivers
import com.paranid5.crescendo.services.stream_service.receivers.unregisterReceivers
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
    ConnectionManager by ConnectionManagerImpl() {
    @Suppress("IncorrectFormatting")
    companion object {
        private const val SERVICE_LOCATION = "com.paranid5.crescendo.services.stream_service"

        const val Broadcast_PAUSE = "$SERVICE_LOCATION.PAUSE"
        const val Broadcast_RESUME = "$SERVICE_LOCATION.RESUME"
        const val Broadcast_SWITCH_VIDEO = "$SERVICE_LOCATION.SWITCH_VIDEO"

        const val Broadcast_10_SECS_BACK = "$SERVICE_LOCATION.10_SECS_BACK"
        const val Broadcast_10_SECS_FORWARD = "$SERVICE_LOCATION.10_SECS_FORWARD"
        const val Broadcast_SEEK_TO = "$SERVICE_LOCATION.SEEK_TO"

        const val Broadcast_CHANGE_REPEAT = "$SERVICE_LOCATION.CHANGE_REPEAT"
        const val Broadcast_DISMISS_NOTIFICATION = "$SERVICE_LOCATION.DISMISS_NOTIFICATION"
        const val Broadcast_STOP = "$SERVICE_LOCATION.STOP"

        const val URL_ARG = "url"
        const val POSITION_ARG = "position"
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

    internal val pauseReceiver = PauseReceiver(this)
    internal val resumeReceiver = ResumeReceiver(this)
    internal val switchVideoReceiver = SwitchVideoReceiver(this)
    internal val tenSecsBackReceiver = TenSecsBackReceiver(this)
    internal val tenSecsForwardReceiver = TenSecsForwardReceiver(this)
    internal val seekToReceiver = SeekToReceiver(this)
    internal val repeatChangedReceiver = RepeatChangedReceiver(this)
    internal val dismissNotificationReceiver = DismissNotificationReceiver(this)
    internal val stopReceiver = StopReceiver(this)

    override fun onCreate() {
        super.onCreate()
        registerReceivers()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        connect(startId)

        mediaSessionManager.initMediaSession(
            context = this@StreamService2,
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
    serviceScope.launch { startMetadataMonitoring() }
    serviceScope.launch { startPlaybackEffectsMonitoring() }
    serviceScope.launch { startEqMonitoring() }
    serviceScope.launch { startBassMonitoring() }
    serviceScope.launch { startReverbMonitoring() }
}

internal inline val Intent.urlArgOrNull
    get() = getStringExtra(StreamService2.URL_ARG)

internal inline val Intent.urlArg
    get() = urlArgOrNull!!

internal inline val Intent.positionArg
    get() = getLongExtra(StreamService2.POSITION_ARG, 0)