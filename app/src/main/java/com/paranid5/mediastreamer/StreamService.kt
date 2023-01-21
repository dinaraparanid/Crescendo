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
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import at.huber.youtubeExtractor.VideoMeta
import com.bumptech.glide.Glide
import com.paranid5.mediastreamer.presentation.MainActivity
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.atomic.AtomicLong

class StreamService : Service(), CoroutineScope by MainScope(), KoinComponent {
    companion object {
        private const val NOTIFICATION_ID = 101
        private const val STREAM_CHANNEL_ID = "stream_channel"
        private const val PLAYBACK_UPDATE_COOLDOWN = 5000L
        private const val TEN_SECS_AS_MILLIS = 10000

        private const val SERVICE_LOCATION = "com.paranid5.mediastreamer.StreamService"
        const val Broadcast_PAUSE = "$SERVICE_LOCATION.PAUSE"
        const val Broadcast_RESUME = "$SERVICE_LOCATION.RESUME"
        const val Broadcast_SWITCH = "$SERVICE_LOCATION.SWITCH"
        const val Broadcast_10_SECS_BACK = "$SERVICE_LOCATION.10_SECS_BACK"
        const val Broadcast_10_SECS_FORWARD = "$SERVICE_LOCATION.10_SECS_FORWARD"
        const val Broadcast_ADD_TO_FAVOURITE = "$SERVICE_LOCATION.ADD_TO_FAVOURITE"
        const val Broadcast_REMOVE_FROM_FAVOURITE = "$SERVICE_LOCATION.REMOVE_FROM_FAVOURITE"
        const val Broadcast_DISMISS_NOTIFICATION = "$SERVICE_LOCATION.DISMISS_NOTIFICATION"

        private const val ACTION_PAUSE = "pause"
        private const val ACTION_RESUME = "resume"
        private const val ACTION_10_SECS_BACK = "back"
        private const val ACTION_10_SECS_FORWARD = "forward"
        private const val ACTION_ADD_TO_FAVOURITE = "add_to_favourite"
        private const val ACTION_REMOVE_FROM_FAVOURITE = "remove_from_favourite"
        private const val ACTION_DISMISS = "dismiss"

        const val URL_ARG = "url"
        private const val ADJUST_PERIOD_TIME_OFFSETS = true
        private val TAG = StreamService::class.simpleName!!
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
            val playbackAction = Intent(
                when (this) {
                    Actions.Resume -> Broadcast_RESUME
                    Actions.Pause -> Broadcast_PAUSE
                    Actions.TenSecsBack -> Broadcast_10_SECS_BACK
                    Actions.TenSecsForward -> Broadcast_10_SECS_FORWARD
                    Actions.AddToFavourite -> Broadcast_ADD_TO_FAVOURITE
                    Actions.RemoveFromFavourite -> Broadcast_REMOVE_FROM_FAVOURITE
                    Actions.Dismiss -> Broadcast_DISMISS_NOTIFICATION
                }
            )

            return PendingIntent.getBroadcast(
                this@StreamService,
                requestCode,
                playbackAction,
                PendingIntent.FLAG_MUTABLE
            )
        }

    private val binder = object : Binder() {}
    private val storageHandler by inject<StorageHandler>()

    private var url = ""
    private var currentMetadata: VideoMeta? = null
    private val currentPlaybackPosition = AtomicLong(0L)
    private lateinit var playbackMonitorTask: Job

    private inline val videoLength
        get() = currentMetadata!!.videoLength * 1000

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

            launch { mShowNotification(isPlaying) }
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            launch { mShowNotification(mIsPlaying) }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            launch { mShowNotification(isPlaying = false) }
            Log.e(TAG, "onPlayerError", error)
        }
    }

    private val pauseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "playback paused")
            mPausePlayback()
        }
    }

    private val resumeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "playback resumed")
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

    private val tenSecsBackReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "10 secs back")
            currentPlaybackPosition
            mSeekTo10SecsBack()
        }
    }

    private val tenSecsForwardReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "10 secs forward")
            mSeekTo10SecsForward()
        }
    }

    private val addToFavouriteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // TODO: add to favourite
        }
    }

    private val removeFromFavouriteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // TODO: remove from favourite
        }
    }

    private val dismissNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mRemoveNotification()
        }
    }

    private fun registerReceivers() {
        registerReceiver(pauseReceiver, IntentFilter(Broadcast_PAUSE))
        registerReceiver(resumeReceiver, IntentFilter(Broadcast_RESUME))
        registerReceiver(switchReceiver, IntentFilter(Broadcast_SWITCH))
        registerReceiver(tenSecsBackReceiver, IntentFilter(Broadcast_10_SECS_BACK))
        registerReceiver(tenSecsForwardReceiver, IntentFilter(Broadcast_10_SECS_FORWARD))
        registerReceiver(addToFavouriteReceiver, IntentFilter(Broadcast_ADD_TO_FAVOURITE))
        registerReceiver(removeFromFavouriteReceiver, IntentFilter(Broadcast_REMOVE_FROM_FAVOURITE))
        registerReceiver(dismissNotificationReceiver, IntentFilter(Broadcast_DISMISS_NOTIFICATION))
    }

    override fun onCreate() {
        super.onCreate()
        registerReceivers()
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

    private fun unregisterReceivers() {
        unregisterReceiver(pauseReceiver)
        unregisterReceiver(resumeReceiver)
        unregisterReceiver(switchReceiver)
        unregisterReceiver(tenSecsBackReceiver)
        unregisterReceiver(tenSecsForwardReceiver)
        unregisterReceiver(addToFavouriteReceiver)
        unregisterReceiver(removeFromFavouriteReceiver)
        unregisterReceiver(dismissNotificationReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMedia()
        mRemoveNotification()
        unregisterReceivers()
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

    private fun updatePlaybackPosition() = currentPlaybackPosition.set(player.currentPosition)

    internal fun mPausePlayback() {
        updatePlaybackPosition()
        player.pause()
    }

    internal fun mResumePlayback() {
        player.playWhenReady = true
    }

    internal suspend inline fun mStoreCurrentUrl(url: String) =
        storageHandler.storeCurrentUrl(url)

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun playStream(url: String) =
        YoutubeAudioUrlExtractor(context = this) { audioUrl, videoUrl, videoMeta ->
            val audioSource = ProgressiveMediaSource
                .Factory(DefaultHttpDataSource.Factory())
                .createMediaSource(MediaItem.fromUri(audioUrl))

            // TODO: video source

            /*val videoSource = ProgressiveMediaSource
                .Factory(DefaultHttpDataSource.Factory())
                .createMediaSource(MediaItem.fromUri(videoUrl))*/

            currentMetadata = videoMeta

            player.run {
                setMediaSource(
                    /*MergingMediaSource(
                        ADJUST_PERIOD_TIME_OFFSETS,
                        audioSource, videoSource
                    )*/
                    audioSource
                )
                playWhenReady = true
                prepare()
            }
        }.extract(url)

    internal fun mPlayNewStream(newUrl: String) {
        url = newUrl
        playStream(newUrl)
    }

    private inline val Intent.urlArgOrNull
        get() = getStringExtra(URL_ARG)

    internal inline val Intent.mUrlArg
        get() = getStringExtra(URL_ARG)!!

    internal fun mRestartPlayer() = playStream(url)

    internal fun mSeekTo10SecsBack() {
        updatePlaybackPosition()
        player.seekTo(maxOf(currentPlaybackPosition.get() - TEN_SECS_AS_MILLIS, 0))
    }

    internal fun mSeekTo10SecsForward() {
        updatePlaybackPosition()
        player.seekTo(minOf(currentPlaybackPosition.get() + TEN_SECS_AS_MILLIS, videoLength))
    }

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

    private suspend inline fun getVideoThumbnailAsync() = coroutineScope {
        async(Dispatchers.IO) {
            currentMetadata
                ?.maxResImageUrl
                ?.let { url ->
                    Glide.with(this@StreamService)
                        .asBitmap()
                        .load(url)
                        .submit()
                        .get()
                }
        }
    }

    // --------------------------- Notification for Oreo+ ---------------------------

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend inline fun Notification.Builder.setContent() = this
        .setShowWhen(false)
        .setSmallIcon(R.drawable.stream_icon)
        .setLargeIcon(getVideoThumbnailAsync().await())
        .setContentTitle(
            currentMetadata?.title
                ?: resources.getString(R.string.stream_no_name)
        )
        .setContentText(
            currentMetadata?.author
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
    private fun getNotificationBuilderOreoAsync(isPlaying: Boolean) = async {
        Notification
            .Builder(applicationContext, STREAM_CHANNEL_ID)
            .setContent()
            .setActions(isPlaying)
    }

    // --------------------------- Notification Compat ---------------------------

    private suspend inline fun NotificationCompat.Builder.setContent() = this
        .setShowWhen(false)
        .setSmallIcon(R.drawable.stream_icon)
        .setLargeIcon(getVideoThumbnailAsync().await())
        .setContentTitle(
            currentMetadata?.title
                ?: resources.getString(R.string.stream_no_name)
        )
        .setContentText(
            currentMetadata?.author
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

    private fun getNotificationBuilderCompatAsync(isPlaying: Boolean) = async {
        NotificationCompat
            .Builder(applicationContext, STREAM_CHANNEL_ID)
            .setContent()
            .setActions(isPlaying)
    }

    // --------------------------- Handle Notification ---------------------------

    internal suspend inline fun mShowNotification(isPlaying: Boolean) = startForeground(
        NOTIFICATION_ID,
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                getNotificationBuilderOreoAsync(isPlaying).await().build()
            else -> getNotificationBuilderCompatAsync(isPlaying).await().build()
        }
    )

    internal fun mRemoveNotification() = when {
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