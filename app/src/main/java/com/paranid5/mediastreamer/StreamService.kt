package com.paranid5.mediastreamer

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.media.AudioManager
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Binder
import android.os.Build
import android.os.SystemClock
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.*
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.paranid5.mediastreamer.utils.extensions.toBitmap

class StreamService : Service() {
    companion object {
        private const val NOTIFICATION_ID = 101
        private const val STREAM_CHANNEL_ID = "stream_channel"

        const val Broadcast_PAUSE = "com.paranid5.mediastreamer.StreamService.PAUSE"
        const val Broadcast_RESUME = "com.paranid5.mediastreamer.StreamService.RESUME"
        const val Broadcast_SWITCH = "com.paranid5.mediastreamer.StreamService.SWITCH"

        const val URL_ARG = "url"

        const val ACTION_PAUSE = "pause"
        const val ACTION_RESUME = "resume"
        const val ACTION_SEEK_BACK = "move_back"
        const val ACTION_SEEK_FORWARD = "move_forward"
        const val ACTION_DISMISS = "dismiss"
    }

    sealed class Actions(val requestCode: Int) {
        object Pause : Actions(100)
        object Resume : Actions(101)
        object SeekBack : Actions(102)
        object SeekForward : Actions(103)
        object Dismiss : Actions(104)
    }

    private inline val Actions.playbackIntent: PendingIntent
        get() {
            val playbackAction = Intent(this@StreamService, StreamService::class.java).also {
                it.action = when (this) {
                    Actions.Resume -> ACTION_RESUME             // Resume
                    Actions.Pause -> ACTION_PAUSE               // Pause
                    Actions.SeekBack -> ACTION_SEEK_BACK        // Seek to 10 secs earlier
                    Actions.SeekForward -> ACTION_SEEK_FORWARD  // Seek to 10 secs later
                    Actions.Dismiss -> ACTION_DISMISS           // Remove notification
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
    private var url = ""
    private var currentPlaybackPosition = 0L

    private var _currentMediaItem: MediaItem? = null
    private val currentMediaItem
        get() = _currentMediaItem!!

    private inline val currentMetadata
        get() = _currentMediaItem!!.mediaMetadata

    private var _mediaSessionManager: MediaSessionManager? = null
    private val mediaSessionManager
        get() = _mediaSessionManager!!

    private var _mediaSession: MediaSession? = null
    private val mediaSession
        get() = _mediaSession!!

    private var _transportControls: MediaController.TransportControls? = null
    private val transportControls
        get() = _transportControls!!

    private var _audioManager: AudioManager? = null
    private val audioManager
        get() = _audioManager!!

    private var _player: ExoPlayer? = ExoPlayer.Builder(applicationContext)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(USAGE_MEDIA)
                .build(),
            true
        )
        .setHandleAudioBecomingNoisy(true)
        .setWakeMode(WAKE_MODE_NETWORK)
        .build()

    private inline val player
        get() = _player!!

    private inline val isPlaying
        get() = _player!!.isPlaying

    private val pauseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            pausePlayback()
        }
    }

    private val resumeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            resumePlayback()
        }
    }

    private val switchReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            playNewStream(intent)
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

        playNewStream(intent)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()

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

    private fun pausePlayback() = player.pause()

    private fun resumePlayback() {
        player.playWhenReady = true
    }

    private fun playNewStream(intent: Intent) {
        url = intent.getStringExtra(URL_ARG)!!
        val mediaItem = MediaItem.fromUri(url)

        player.run {
            setMediaItem(mediaItem)
            playWhenReady = true
            prepare()
        }

        buildNotification()
    }

    private fun releasePlayer() {
        _player?.release()
        _player = null
    }

    private inline val newMediaSessionCallback
        get() = object : MediaSession.Callback() {
            override fun onPlay() {
                super.onPlay()
                resumePlayback()
            }

            override fun onPause() {
                super.onPause()
                pausePlayback()
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
                currentPlaybackPosition,
                1.0F,
                SystemClock.elapsedRealtime()
            )
            .build()

    private fun initMediaSession() {
        _mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE)!! as MediaSessionManager
        _mediaSession = MediaSession(applicationContext, "StreamService")
        _transportControls = mediaSession.controller.transportControls

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
    private fun Notification.Builder.setActions() = this
        .addAction(
            Notification.Action.Builder(
                Icon.createWithResource("", R.drawable.prev_track),
                ACTION_SEEK_BACK,
                Actions.SeekBack.playbackIntent
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
                ACTION_SEEK_FORWARD,
                Actions.SeekForward.playbackIntent
            ).build()
        )

    private inline val notificationBuilderOreo
        @RequiresApi(Build.VERSION_CODES.O)
        get() = Notification
            .Builder(applicationContext, STREAM_CHANNEL_ID)
            .setContent()
            .setActions()

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
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

    private fun NotificationCompat.Builder.setActions() = this
        .addAction(
            NotificationCompat.Action.Builder(
                IconCompat.createWithResource(this@StreamService, R.drawable.prev_track),
                ACTION_SEEK_BACK,
                Actions.SeekBack.playbackIntent
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
                ACTION_SEEK_FORWARD,
                Actions.SeekForward.playbackIntent
            ).build()
        )

    private inline val notificationBuilderCompat
        get() = NotificationCompat
            .Builder(applicationContext, STREAM_CHANNEL_ID)
            .setContent()
            .setActions()

    private fun buildNotification() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> notificationBuilderOreo.build()
            else -> notificationBuilderCompat.build()
        }
    }

    private fun removeNotification() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> stopForeground(STOP_FOREGROUND_REMOVE)
        else -> stopForeground(true)
    }
}