package com.paranid5.crescendo.system.services.stream

import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import com.paranid5.crescendo.system.common.broadcast.StreamServiceBroadcasts.POSITION_ARG
import com.paranid5.crescendo.system.common.broadcast.StreamServiceBroadcasts.URL_ARG
import com.paranid5.crescendo.system.services.stream.extractor.UrlExtractor
import com.paranid5.crescendo.system.services.stream.media_session.MediaSessionCallback
import com.paranid5.crescendo.system.services.stream.notification.NotificationManager
import com.paranid5.crescendo.system.services.stream.playback.PlayerProvider
import com.paranid5.crescendo.system.services.stream.playback.startBassMonitoring
import com.paranid5.crescendo.system.services.stream.playback.startEqMonitoring
import com.paranid5.crescendo.system.services.stream.playback.startPlaybackEffectsMonitoring
import com.paranid5.crescendo.system.services.stream.playback.startPlaybackEventLoop
import com.paranid5.crescendo.system.services.stream.playback.startResumingAsync
import com.paranid5.crescendo.system.services.stream.playback.startReverbMonitoring
import com.paranid5.crescendo.system.services.stream.playback.startStreamAsync
import com.paranid5.crescendo.system.services.stream.receivers.PauseReceiver
import com.paranid5.crescendo.system.services.stream.receivers.RepeatChangedReceiver
import com.paranid5.crescendo.system.services.stream.receivers.ResumeReceiver
import com.paranid5.crescendo.system.services.stream.receivers.SeekToReceiver
import com.paranid5.crescendo.system.services.stream.receivers.SwitchVideoReceiver
import com.paranid5.crescendo.system.services.stream.receivers.TenSecsBackReceiver
import com.paranid5.crescendo.system.services.stream.receivers.TenSecsForwardReceiver
import com.paranid5.crescendo.system.services.stream.receivers.registerReceivers
import com.paranid5.crescendo.system.services.stream.receivers.unregisterReceivers
import com.paranid5.system.services.common.ConnectionManager
import com.paranid5.system.services.common.MediaSuspendService
import com.paranid5.system.services.common.PlaybackForegroundService
import com.paranid5.system.services.common.connect
import com.paranid5.system.services.common.disconnect
import com.paranid5.system.services.common.media_session.MediaSessionManager
import com.paranid5.system.services.common.notification.detachNotification
import com.paranid5.system.services.common.receivers.DismissNotificationReceiver
import com.paranid5.system.services.common.receivers.StopReceiver
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

private const val ACTION_PAUSE = "pause"
private const val ACTION_RESUME = "resume"
private const val ACTION_10_SECS_BACK = "back"
private const val ACTION_10_SECS_FORWARD = "forward"

internal const val ACTION_REPEAT = "repeat"
internal const val ACTION_UNREPEAT = "unrepeat"
internal const val ACTION_DISMISS = "dismiss"

private const val MEDIA_SESSION_ID = "stream_media_session_id"

class StreamService : MediaSuspendService(), PlaybackForegroundService, KoinComponent,
    ConnectionManager by ConnectionManagerImpl() {
    internal val mediaSessionManager by inject<MediaSessionManager>()
    internal val playerProvider by inject<PlayerProvider> { parametersOf(this) }
    internal val urlExtractor by inject<UrlExtractor>()
    internal val notificationManager by inject<NotificationManager> { parametersOf(this) }

    internal val commandsToActions = mapOf(
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
    internal val seekToReceiver = SeekToReceiver(this)
    internal val tenSecsBackReceiver = TenSecsBackReceiver(this)
    internal val tenSecsForwardReceiver = TenSecsForwardReceiver(this)
    internal val repeatChangedReceiver = RepeatChangedReceiver(this)
    internal val dismissNotificationReceiver = DismissNotificationReceiver(this)
    internal val stopReceiver = StopReceiver(this)

    override fun onCreate() {
        super.onCreate()
        registerReceivers()

        mediaSessionManager.initMediaSession(
            context = this,
            player = playerProvider.player,
            mediaSessionId = MEDIA_SESSION_ID,
            callback = MediaSessionCallback(service = this),
        )

        notificationManager.initNotificationManager(playerProvider.player)

        launchMonitoringTasks()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        connect(startId)

        when (val url = intent?.urlArgOrNull) {
            // Continue with previous stream
            null -> startResumingAsync()

            // New stream
            else -> startStreamAsync(url)
        }

        return START_STICKY
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) =
        mediaSessionManager.mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = playerProvider.player

        if (
            player.playWhenReady.not() ||
            player.mediaItemCount == 0 ||
            player.playbackState == Player.STATE_ENDED
        ) {
            stopSelf()
        }
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

private fun StreamService.launchMonitoringTasks() {
    serviceScope.launch { startPlaybackEventLoop() }
    serviceScope.launch { startPlaybackEffectsMonitoring() }
    serviceScope.launch { startEqMonitoring() }
    serviceScope.launch { startBassMonitoring() }
    serviceScope.launch { startReverbMonitoring() }
}

private inline val Intent.urlArgOrNull
    get() = getStringExtra(URL_ARG)

internal inline val Intent.urlArg
    get() = urlArgOrNull!!

internal inline val Intent.positionArg
    get() = getLongExtra(POSITION_ARG, 0)