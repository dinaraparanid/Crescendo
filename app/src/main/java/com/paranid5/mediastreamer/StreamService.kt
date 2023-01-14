package com.paranid5.mediastreamer

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Binder
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.*
import androidx.media3.common.C.*
import androidx.media3.exoplayer.ExoPlayer
import com.paranid5.mediastreamer.utils.extensions.toBitmap
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.atomic.AtomicLong

class StreamService : Service(), CoroutineScope by MainScope(), KoinComponent {
    companion object {
        private const val NOTIFICATION_ID = 101
        private const val STREAM_CHANNEL_ID = "stream_channel"
        private const val PLAYBACK_UPDATE_COOLDOWN = 5000L

        const val Broadcast_PAUSE = "com.paranid5.mediastreamer.StreamService.PAUSE"
        const val Broadcast_RESUME = "com.paranid5.mediastreamer.StreamService.RESUME"
        const val Broadcast_SWITCH = "com.paranid5.mediastreamer.StreamService.SWITCH"

        const val URL_ARG = "url"

        const val ACTION_PAUSE = "pause"
        const val ACTION_RESUME = "resume"
        const val ACTION_10_SECS_BACK = "back"
        const val ACTION_10_SECS_FORWARD = "forward"
        const val ACTION_ADD_TO_FAVOURITE = "add_to_favourite"
        const val ACTION_REMOVE_FROM_FAVOURITE = "remove_from_favourite"
        const val ACTION_DISMISS = "dismiss"
    }

    sealed class Actions(val requestCode: Int) {
        object Pause : Actions(NOTIFICATION_ID + 1)
        object Resume : Actions(NOTIFICATION_ID + 2)
        object TenSecsBack : Actions(NOTIFICATION_ID + 3)
        object TenSecsForward : Actions(NOTIFICATION_ID + 4)
        object AddToFavourite : Actions(NOTIFICATION_ID + 5)
        object RemoveFromFavourite : Actions(NOTIFICATION_ID + 6)
        object Dismiss : Actions(NOTIFICATION_ID + 7)
    }

    private inline val Actions.playbackIntent: PendingIntent
        get() {
            val playbackAction = Intent(this@StreamService, StreamService::class.java).also {
                it.action = when (this) {
                    Actions.Resume -> ACTION_RESUME
                    Actions.Pause -> ACTION_PAUSE
                    Actions.TenSecsBack -> ACTION_10_SECS_BACK
                    Actions.TenSecsForward -> ACTION_10_SECS_FORWARD
                    Actions.AddToFavourite -> ACTION_ADD_TO_FAVOURITE
                    Actions.RemoveFromFavourite -> ACTION_REMOVE_FROM_FAVOURITE
                    Actions.Dismiss -> ACTION_DISMISS
                }
            }

            return when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                    PendingIntent.getForegroundService(
                        this@StreamService,
                        requestCode,
                        playbackAction,
                        PendingIntent.FLAG_MUTABLE
                    )

                else -> PendingIntent.getService(
                    this@StreamService,
                    requestCode,
                    playbackAction,
                    PendingIntent.FLAG_MUTABLE
                )
            }
        }

    private val binder = object : Binder() {}
    private val storageHandler by inject<StorageHandler>()

    private var url = ""
    private val currentPlaybackPosition = AtomicLong(0L)
    private lateinit var playbackMonitorTask: Job

    private lateinit var currentMediaItem: MediaItem
    private inline val currentMetadata
        get() = currentMediaItem.mediaMetadata

    // ----------------------- Media session management -----------------------

    private lateinit var mediaSessionManager: MediaSessionManager
    private lateinit var mediaSession: MediaSession
    private lateinit var transportControls: MediaController.TransportControls

    private val player by lazy {
        ExoPlayer.Builder(applicationContext)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AUDIO_CONTENT_TYPE_MOVIE)
                    .setUsage(USAGE_MEDIA)
                    .build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(WAKE_MODE_NETWORK)
            .build()
            .apply { addListener(mPlayerStateChangedListener) }
    }

    internal inline val mIsPlaying
        get() = player.isPlaying

    private val mPlayerStateChangedListener: Player.Listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (playbackState == Player.STATE_IDLE)
                mRestartPlayer()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)

            when {
                isPlaying -> mStartPlaybackMonitoring()
                else -> mStopPlaybackMonitoring()
            }

            mShowNotification(isPlaying)
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            mShowNotification(mIsPlaying)
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            mShowNotification(isPlaying = false)
            Log.e("StreamService", "onPlayerError", error)
        }
    }

    private val pauseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mPausePlayback()
        }
    }

    private val resumeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mResumePlayback()
        }
    }

    private val switchReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val url = intent.mUrlArg
            launch { mStoreCurrentUrl(url) }
            mPlayNewStream(url)
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(pauseReceiver, IntentFilter(Broadcast_PAUSE))
        registerReceiver(resumeReceiver, IntentFilter(Broadcast_RESUME))
        registerReceiver(switchReceiver, IntentFilter(Broadcast_SWITCH))
    }

    override fun onBind(intent: Intent?) = binder

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel()

        initMediaSession()

        intent.urlArgOrNull?.let { url ->
            // New stream
            launch { mStoreCurrentUrl(url) }
            mPlayNewStream(url)
        } ?: launch {
            // Continue previous stream
            storageHandler.currentUrl.collect { url ->
                mPlayNewStream(url!!)
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMedia()
        removeNotification()

        unregisterReceiver(pauseReceiver)
        unregisterReceiver(resumeReceiver)
        unregisterReceiver(switchReceiver)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() =
        (getSystemService(NOTIFICATION_SERVICE)!! as NotificationManager)
            .createNotificationChannel(
                NotificationChannel(
                    STREAM_CHANNEL_ID,
                    "Stream",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    setShowBadge(true)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    setSound(null, null)
                }
            )

    internal fun mPausePlayback() {
        currentPlaybackPosition.set(player.currentPosition)
        player.pause()
    }

    internal fun mResumePlayback() {
        player.playWhenReady = true
    }

    internal suspend inline fun mStoreCurrentUrl(url: String) =
        storageHandler.storeCurrentUrl(url)

    internal fun mPlayNewStream(newUrl: String) {
        url = newUrl
        currentMediaItem = MediaItem.fromUri(url)

        player.run {
            setMediaItem(this@StreamService.currentMediaItem)
            playWhenReady = true
            prepare()
        }
    }

    private inline val Intent.urlArgOrNull
        get() = getStringExtra(URL_ARG)

    internal inline val Intent.mUrlArg
        get() = getStringExtra(URL_ARG)!!

    internal fun mRestartPlayer() =
        mPlayNewStream(newUrl = url)

    private fun releaseMedia() {
        player.stop()
        player.release()
        mediaSession.release()
        transportControls.stop()
    }

    private inline val newMediaSessionCallback
        get() = object : MediaSession.Callback() {
            override fun onPlay() {
                super.onPlay()
                mResumePlayback()
            }

            override fun onPause() {
                super.onPause()
                mPausePlayback()
            }
        }

    private val playbackStateActions = (
            PlaybackState.ACTION_PLAY
                    or PlaybackState.ACTION_PLAY_PAUSE
                    or PlaybackState.ACTION_PLAY_FROM_MEDIA_ID
                    or PlaybackState.ACTION_PAUSE
            ).let { actions ->
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                    actions or PlaybackState.ACTION_PLAY_FROM_URI

                else -> actions
            }
        }

    private inline val newPlaybackState
        get() = PlaybackState.Builder()
            .setActions(playbackStateActions)
            .setState(
                PlaybackState.STATE_PAUSED,
                currentPlaybackPosition.get(),
                1.0F,
                SystemClock.elapsedRealtime()
            )
            .build()

    private fun initMediaSession() {
        mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE)!! as MediaSessionManager
        mediaSession = MediaSession(applicationContext, "StreamService")
        transportControls = mediaSession.controller.transportControls

        mediaSession.run {
            isActive = true

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) setFlags(
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS
                        or MediaSession.FLAG_HANDLES_MEDIA_BUTTONS
            )

            setCallback(newMediaSessionCallback)
            setPlaybackState(newPlaybackState)
        }
    }

    // --------------------------- Notification for Oreo+ ---------------------------

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Notification.Builder.setContent() = this
        .setShowWhen(false)
        .setSmallIcon(R.drawable.stream)
        .setLargeIcon(currentMetadata.artworkData?.toBitmap())
        .setContentTitle(
            currentMetadata.title
                ?: resources.getString(R.string.stream_no_name)
        )
        .setContentText(
            currentMetadata.artist
                ?: resources.getString(R.string.unknown_streamer)
        )
        .setStyle(
            Notification.MediaStyle()
                .setMediaSession(mediaSession.sessionToken)
                .setShowActionsInCompactView()
        )
        .setContentIntent(
            PendingIntent.getActivity(
                applicationContext,
                0,
                Intent(applicationContext, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Notification.Builder.setActions(isPlaying: Boolean) = this
        .addAction(
            Notification.Action.Builder(
                Icon.createWithResource("", R.drawable.prev_track),
                ACTION_10_SECS_BACK,
                Actions.TenSecsBack.playbackIntent
            ).build()
        )
        .addAction(
            when {
                isPlaying -> Notification.Action.Builder(
                    Icon.createWithResource("", R.drawable.pause),
                    ACTION_PAUSE,
                    Actions.Pause.playbackIntent
                )

                else -> Notification.Action.Builder(
                    Icon.createWithResource("", R.drawable.play),
                    ACTION_RESUME,
                    Actions.Resume.playbackIntent
                )
            }.build()
        )
        .addAction(
            Notification.Action.Builder(
                Icon.createWithResource("", R.drawable.next_track),
                ACTION_10_SECS_FORWARD,
                Actions.TenSecsForward.playbackIntent
            ).build()
        )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNotificationBuilderOreo(isPlaying: Boolean) =
        Notification
            .Builder(applicationContext, STREAM_CHANNEL_ID)
            .setContent()
            .setActions(isPlaying)

    // --------------------------- Notification Compat ---------------------------

    private fun NotificationCompat.Builder.setContent() = this
        .setShowWhen(false)
        .setSmallIcon(R.drawable.stream)
        .setLargeIcon(currentMetadata.artworkData?.toBitmap())
        .setContentTitle(
            currentMetadata.title
                ?: resources.getString(R.string.stream_no_name)
        )
        .setContentText(
            currentMetadata.artist
                ?: resources.getString(R.string.unknown_streamer)
        )
        .setContentIntent(
            PendingIntent.getActivity(
                applicationContext,
                0,
                Intent(applicationContext, MainActivity::class.java),
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                    PendingIntent.FLAG_UPDATE_CURRENT
                else
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

    private fun NotificationCompat.Builder.setActions(isPlaying: Boolean) = this
        .addAction(
            NotificationCompat.Action.Builder(
                IconCompat.createWithResource(this@StreamService, R.drawable.prev_track),
                ACTION_10_SECS_BACK,
                Actions.TenSecsBack.playbackIntent
            ).build()
        )
        .addAction(
            when {
                isPlaying -> NotificationCompat.Action.Builder(
                    IconCompat.createWithResource(this@StreamService, R.drawable.pause),
                    ACTION_PAUSE,
                    Actions.Pause.playbackIntent
                )

                else -> NotificationCompat.Action.Builder(
                    IconCompat.createWithResource(this@StreamService, R.drawable.play),
                    ACTION_RESUME,
                    Actions.Resume.playbackIntent
                )
            }.build()
        )
        .addAction(
            NotificationCompat.Action.Builder(
                IconCompat.createWithResource(this@StreamService, R.drawable.next_track),
                ACTION_10_SECS_FORWARD,
                Actions.TenSecsForward.playbackIntent
            ).build()
        )

    private fun getNotificationBuilderCompat(isPlaying: Boolean) =
        NotificationCompat
            .Builder(applicationContext, STREAM_CHANNEL_ID)
            .setContent()
            .setActions(isPlaying)

    // --------------------------- Handle Notification ---------------------------

    internal fun mShowNotification(isPlaying: Boolean) = startForeground(
        NOTIFICATION_ID,
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                getNotificationBuilderOreo(isPlaying).build()
            else -> getNotificationBuilderCompat(isPlaying).build()
        }
    )

    private fun removeNotification() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> stopForeground(STOP_FOREGROUND_REMOVE)
        else -> stopForeground(true)
    }

    // --------------------------- Playback Monitoring ---------------------------

    internal fun mStartPlaybackMonitoring() {
        playbackMonitorTask = launch {
            while (true) {
                delay(PLAYBACK_UPDATE_COOLDOWN)

                val currentPosition = player.currentPosition
                currentPlaybackPosition.set(currentPosition)
                storageHandler.storePlaybackPosition(currentPosition)
            }
        }
    }

    internal fun mStopPlaybackMonitoring() = playbackMonitorTask.cancel()
}