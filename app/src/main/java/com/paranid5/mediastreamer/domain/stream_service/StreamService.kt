package com.paranid5.mediastreamer.domain.stream_service

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.*
import androidx.media3.common.C.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.MediaNotification
import arrow.core.Either
import at.huber.youtubeExtractor.VideoMeta
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.domain.YoutubeUrlExtractor
import com.paranid5.mediastreamer.data.VideoMetadata
import com.paranid5.mediastreamer.data.utils.extensions.toAndroidMetadata
import com.paranid5.mediastreamer.presentation.MainActivity
import com.paranid5.mediastreamer.presentation.streaming.*
import com.paranid5.mediastreamer.presentation.ui.screens.*
import com.paranid5.mediastreamer.presentation.ui.GlideUtils
import com.paranid5.mediastreamer.domain.utils.extensions.registerReceiverCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

@OptIn(androidx.media3.common.util.UnstableApi::class)
class StreamService : Service(), CoroutineScope by MainScope(), KoinComponent {
    companion object {
        private const val NOTIFICATION_ID = 101
        private const val STREAM_CHANNEL_ID = "stream_channel"
        private const val PLAYBACK_UPDATE_COOLDOWN = 500L
        private const val TEN_SECS_AS_MILLIS = 10000

        private const val SERVICE_LOCATION = "com.paranid5.mediastreamer.stream_service"
        const val Broadcast_PAUSE = "$SERVICE_LOCATION.PAUSE"
        const val Broadcast_RESUME = "$SERVICE_LOCATION.RESUME"
        const val Broadcast_SWITCH_VIDEO = "$SERVICE_LOCATION.SWITCH_VIDEO"
        const val Broadcast_10_SECS_BACK = "$SERVICE_LOCATION.10_SECS_BACK"
        const val Broadcast_10_SECS_FORWARD = "$SERVICE_LOCATION.10_SECS_FORWARD"
        const val Broadcast_SEEK_TO = "$SERVICE_LOCATION.SEEK_TO"
        const val Broadcast_ADD_TO_FAVOURITE = "$SERVICE_LOCATION.ADD_TO_FAVOURITE"
        const val Broadcast_REMOVE_FROM_FAVOURITE = "$SERVICE_LOCATION.REMOVE_FROM_FAVOURITE"
        const val Broadcast_CHANGE_REPEAT = "$SERVICE_LOCATION.CHANGE_REPEAT"
        const val Broadcast_DISMISS_NOTIFICATION = "$SERVICE_LOCATION.DISMISS_NOTIFICATION"

        private const val ACTION_PAUSE = "pause"
        private const val ACTION_RESUME = "resume"
        private const val ACTION_10_SECS_BACK = "back"
        private const val ACTION_10_SECS_FORWARD = "forward"
        private const val ACTION_ADD_TO_FAVOURITE = "add_to_favourite"
        private const val ACTION_REMOVE_FROM_FAVOURITE = "remove_from_favourite"
        private const val ACTION_CHANGE_REPEAT = "change_repeat"
        private const val ACTION_DISMISS = "dismiss" // TODO: Dismiss notification

        const val URL_ARG = "url"
        const val POSITION_ARG = "position"

        private const val ADJUST_PERIOD_TIME_OFFSETS = true
        private val TAG = StreamService::class.simpleName!!

        internal inline val Intent.mUrlArgOrNull
            get() = getStringExtra(URL_ARG)

        internal inline val Intent.mUrlArg
            get() = mUrlArgOrNull!!
    }

    sealed class Actions(val requestCode: Int, val playbackAction: String) {
        object Pause : Actions(
            requestCode = NOTIFICATION_ID + 1,
            playbackAction = Broadcast_PAUSE
        )

        object Resume : Actions(
            requestCode = NOTIFICATION_ID + 2,
            playbackAction = Broadcast_RESUME
        )

        object TenSecsBack : Actions(
            requestCode = NOTIFICATION_ID + 3,
            playbackAction = Broadcast_10_SECS_BACK
        )

        object TenSecsForward : Actions(
            requestCode = NOTIFICATION_ID + 4,
            playbackAction = Broadcast_10_SECS_FORWARD
        )

        object AddToFavourite : Actions(
            requestCode = NOTIFICATION_ID + 5,
            playbackAction = Broadcast_ADD_TO_FAVOURITE
        )

        object RemoveFromFavourite : Actions(
            requestCode = NOTIFICATION_ID + 6,
            playbackAction = Broadcast_REMOVE_FROM_FAVOURITE
        )

        object ChangeRepeat : Actions(
            requestCode = NOTIFICATION_ID + 7,
            playbackAction = Broadcast_CHANGE_REPEAT
        )

        object Dismiss : Actions(
            requestCode = NOTIFICATION_ID + 8,
            playbackAction = Broadcast_DISMISS_NOTIFICATION
        )
    }

    private inline val Actions.playbackIntent: PendingIntent
        get() = PendingIntent.getBroadcast(
            this@StreamService,
            requestCode,
            Intent(playbackAction),
            PendingIntent.FLAG_MUTABLE
        )

    private val binder = object : Binder() {}
    private val storageHandler by inject<StorageHandler>()
    private val glideUtils by inject<GlideUtils> { parametersOf(this) }
    private val currentMetadata = MutableStateFlow<VideoMetadata?>(null)
    private lateinit var playbackMonitorTask: Job

    @kotlin.OptIn(ExperimentalCoroutinesApi::class)
    private val videoLength = currentMetadata
        .mapLatest { it?.lenInMillis ?: 0 }
        .stateIn(this, SharingStarted.Eagerly, 0)

    private inline val currentPlaybackPosition
        get() = player.currentPosition

    // TODO: favourite database
    private val isCurrentVideoLikedState = MutableStateFlow(false)

    internal inline val mIsCurrentVideoLiked
        get() = isCurrentVideoLikedState.value

    internal inline val mIsRepeating
        get() = storageHandler.isRepeatingState.value

    internal inline val mCurrentUrl
        get() = storageHandler.currentUrlState.value

    @Volatile
    private var isNotificationShown = false

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private val notificationBuilder by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Either.Right(notificationBuilderOreo)
        else Either.Left(notificationBuilderCompat)
    }

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
            .apply { addListener(playerStateChangedListener) }
    }

    internal inline val mIsPlaying
        get() = player.isPlaying

    private val playerStateChangedListener: Player.Listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)

            when (playbackState) {
                Player.STATE_IDLE -> launch { mRestartPlayer() }

                Player.STATE_ENDED -> when {
                    mIsRepeating -> launch { mRestartPlayer() }
                    else -> stopSelf()
                }

                else -> Unit
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)

            sendBroadcast(
                Intent(Broadcast_IS_PLAYING_CHANGED)
                    .putExtra(IS_PLAYING_ARG, isPlaying)
            )

            launch { mUpdateMediaSession() }

            when {
                isPlaying -> mStartPlaybackMonitoring()
                else -> mStopPlaybackMonitoring()
            }

            launch {
                mUpdateOrShowNotification(
                    isPlaying = isPlaying,
                    isLiked = isCurrentVideoLikedState.value
                )
            }
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            launch {
                mUpdateOrShowNotification(
                    isPlaying = mIsPlaying,
                    isLiked = isCurrentVideoLikedState.value
                )
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            launch {
                mUpdateOrShowNotification(
                    isPlaying = false,
                    isLiked = isCurrentVideoLikedState.value
                )
            }
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

    private val switchVideoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val url = intent.mUrlArg
            launch { mStoreCurrentUrl(url) }
            mPlayNewStream(url)
        }
    }

    private val tenSecsBackReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "10 secs back")
            mSeekTo10SecsBack()
        }
    }

    private val tenSecsForwardReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "10 secs forward")
            mSeekTo10SecsForward()
        }
    }

    private val seekToReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val position = intent.getLongExtra(POSITION_ARG, 0)
            Log.d(TAG, "seek to $position")
            mSeekTo(position)
        }
    }

    private val addToFavouriteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "TODO: add to favourite")
            // TODO: add to favourite
        }
    }

    private val removeFromFavouriteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "TODO: remove from favourite")
            // TODO: remove from favourite
        }
    }

    private val repeatChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            launch {
                mStoreAndSendIsRepeating(!mIsRepeating)
                mUpdateNotification(isPlaying = mIsPlaying, isLiked = mIsCurrentVideoLiked)
                Log.d(TAG, "Repeating changed: $mIsRepeating")
            }
        }
    }

    private val dismissNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Notification removed")
            mRemoveNotification()
        }
    }

    private fun registerReceivers() {
        registerReceiverCompat(pauseReceiver, Broadcast_PAUSE)
        registerReceiverCompat(resumeReceiver, Broadcast_RESUME)
        registerReceiverCompat(switchVideoReceiver, Broadcast_SWITCH_VIDEO)
        registerReceiverCompat(tenSecsBackReceiver, Broadcast_10_SECS_BACK)
        registerReceiverCompat(tenSecsForwardReceiver, Broadcast_10_SECS_FORWARD)
        registerReceiverCompat(seekToReceiver, Broadcast_SEEK_TO)
        registerReceiverCompat(addToFavouriteReceiver, Broadcast_ADD_TO_FAVOURITE)
        registerReceiverCompat(removeFromFavouriteReceiver, Broadcast_REMOVE_FROM_FAVOURITE)
        registerReceiverCompat(repeatChangedReceiver, Broadcast_CHANGE_REPEAT)
        registerReceiverCompat(dismissNotificationReceiver, Broadcast_DISMISS_NOTIFICATION)
    }

    override fun onCreate() {
        super.onCreate()
        registerReceivers()
    }

    override fun onBind(intent: Intent?) = binder

    private suspend fun startNotificationObserving(): Unit = currentMetadata.collectLatest {
        Log.d(TAG, "Metadata update, show new notification")
        mUpdateOrShowNotification(
            isPlaying = mIsPlaying,
            isLiked = isCurrentVideoLikedState.value
        )
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel()

        initMediaSession()
        launch { startNotificationObserving() }

        intent.mUrlArgOrNull?.let { url ->
            // New stream
            launch { mStoreCurrentUrl(url) }
            mPlayNewStream(url)
        } ?: launch {
            // Continue with previous stream

            mPlayNewStream(
                url = storageHandler.currentUrlState.value,
                initialPosition = storageHandler.playbackPositionState.value
            )
        }

        return START_REDELIVER_INTENT
    }

    private fun unregisterReceivers() {
        unregisterReceiver(pauseReceiver)
        unregisterReceiver(resumeReceiver)
        unregisterReceiver(switchVideoReceiver)
        unregisterReceiver(tenSecsBackReceiver)
        unregisterReceiver(tenSecsForwardReceiver)
        unregisterReceiver(seekToReceiver)
        unregisterReceiver(addToFavouriteReceiver)
        unregisterReceiver(removeFromFavouriteReceiver)
        unregisterReceiver(repeatChangedReceiver)
        unregisterReceiver(dismissNotificationReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMedia()
        mRemoveNotification()
        unregisterReceivers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() = notificationManager.createNotificationChannel(
        NotificationChannel(
            STREAM_CHANNEL_ID,
            "Stream",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(null, null)
            enableVibration(false)
            enableLights(false)
        }
    )

    private suspend inline fun updateAndSendPlaybackPosition() {
        sendBroadcast(
            Intent(Broadcast_CUR_POSITION_CHANGED)
                .putExtra(CUR_POSITION_ARG, currentPlaybackPosition)
        )

        storePlaybackPosition()
    }

    internal fun mPausePlayback() {
        launch { updateAndSendPlaybackPosition() }
        player.pause()
    }

    internal fun mResumePlayback() {
        player.playWhenReady = true
    }

    private suspend inline fun storePlaybackPosition() =
        storageHandler.storePlaybackPosition(currentPlaybackPosition)

    private suspend inline fun storeIsRepeating(isRepeating: Boolean) =
        storageHandler.storeIsRepeating(isRepeating)

    internal suspend inline fun mStoreAndSendIsRepeating(isRepeating: Boolean) {
        sendBroadcast(
            Intent(Broadcast_IS_REPEATING_CHANGED)
                .putExtra(IS_REPEATING_ARG, isRepeating)
        )
        storeIsRepeating(isRepeating)
    }

    internal suspend inline fun mStoreCurrentUrl(url: String) =
        storageHandler.storeCurrentUrl(url)

    private suspend inline fun storeMetadata(videoMeta: VideoMeta?) =
        storageHandler.storeCurrentMetadata(videoMeta?.let(::VideoMetadata))

    private suspend inline fun storeCurrentUrl(newUrl: String) =
        storageHandler.storeCurrentUrl(newUrl)

    @OptIn(UnstableApi::class)
    private fun playStream(url: String, initialPosition: Long = 0) =
        YoutubeUrlExtractor(context = this) { audioUrl, videoUrl, videoMeta ->
            val audioSource = ProgressiveMediaSource
                .Factory(DefaultHttpDataSource.Factory())
                .createMediaSource(MediaItem.fromUri(audioUrl))

            // TODO: video source

            /*val videoSource = ProgressiveMediaSource
                .Factory(DefaultHttpDataSource.Factory())
                .createMediaSource(MediaItem.fromUri(videoUrl))*/

            mUpdateMediaSession(videoMeta?.let(::VideoMetadata))
            launch(Dispatchers.IO) { storeMetadata(videoMeta) }

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
                seekTo(initialPosition)
            }
        }.extract(url)

    internal fun mPlayNewStream(url: String, initialPosition: Long = 0) {
        launch { storeCurrentUrl(url) }
        playStream(url, initialPosition)
    }

    internal suspend inline fun mRestartPlayer(): Unit =
        storageHandler.currentUrlState.collectLatest { url -> playStream(url) }

    internal fun mSeekTo(position: Long) =
        player.seekTo(position)

    internal fun mSeekTo10SecsBack() =
        player.seekTo(maxOf(currentPlaybackPosition - TEN_SECS_AS_MILLIS, 0))

    internal fun mSeekTo10SecsForward() =
        player.seekTo(minOf(currentPlaybackPosition + TEN_SECS_AS_MILLIS, videoLength.value))

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

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
                mSeekTo(pos)
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                mSeekTo10SecsForward()
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                mSeekTo10SecsBack()
            }

            override fun onCustomAction(action: String, extras: Bundle?) {
                super.onCustomAction(action, extras)

                sendBroadcast(
                    Intent(
                        when (action) {
                            ACTION_CHANGE_REPEAT -> Broadcast_CHANGE_REPEAT
                            ACTION_10_SECS_BACK -> Broadcast_10_SECS_BACK
                            ACTION_PAUSE -> Broadcast_PAUSE
                            ACTION_RESUME -> Broadcast_RESUME
                            ACTION_10_SECS_FORWARD -> Broadcast_10_SECS_FORWARD
                            ACTION_ADD_TO_FAVOURITE -> Broadcast_ADD_TO_FAVOURITE
                            ACTION_REMOVE_FROM_FAVOURITE -> Broadcast_REMOVE_FROM_FAVOURITE
                            else -> throw IllegalArgumentException("Unknown action")
                        }
                    )
                )
            }
        }

    private inline val newPlaybackState
        get() = PlaybackState.Builder()
            .setActions(
                PlaybackState.ACTION_SKIP_TO_PREVIOUS
                        or PlaybackState.ACTION_PLAY
                        or PlaybackState.ACTION_PAUSE
                        or PlaybackState.ACTION_PLAY_PAUSE
                        or PlaybackState.ACTION_SKIP_TO_NEXT
                        or PlaybackState.ACTION_SEEK_TO
            )
            .setState(
                if (mIsPlaying) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED,
                currentPlaybackPosition,
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

    internal suspend fun mUpdateMediaSession(
        videoMetadata: VideoMetadata? = currentMetadata.value
    ) = mediaSession.run {
        setPlaybackState(newPlaybackState)
        setMetadata(
            currentMetadata
                .updateAndGet { videoMetadata }
                ?.toAndroidMetadata(mGetVideoCoverAsync().await())
        )
    }

    internal suspend inline fun mGetVideoCoverAsync() =
        currentMetadata
            .value
            ?.let { glideUtils.getVideoCoverBitmapAsync(it) }
            ?: coroutineScope { async(Dispatchers.IO) { glideUtils.thumbnailBitmap } }

    // --------------------------- Notification for Oreo+ ---------------------------

    @kotlin.OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    private val notificationBuilderOreo = currentMetadata.mapLatest {
        Notification
            .Builder(applicationContext, STREAM_CHANNEL_ID)
            .setContent()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Notification.Builder.setContent() = this
        .setShowWhen(false)
        .setSmallIcon(R.drawable.stream_icon)
        .setAutoCancel(false)
        .setOngoing(true)
        .setStyle(
            Notification.MediaStyle()
                .setMediaSession(mediaSession.sessionToken)
                .setShowActionsInCompactView(1, 2, 3)
        )
        .setContentIntent(
            PendingIntent.getActivity(
                applicationContext,
                0,
                Intent(applicationContext, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

    @RequiresApi(Build.VERSION_CODES.N)
    private fun Notification.Builder.setActions(isPlaying: Boolean, isLiked: Boolean) = this
        .setActions(
            *arrayOf(
                repeatActionOreo,
                tenSecsBackActionIfSeekableOreo,
                getPlayPauseActionOreo(isPlaying),
                tenSecsForwardActionIfSeekableOreo,
                getLikeActionOreo(isLiked)
            ).filterNotNull().toTypedArray()
        )

    private inline val repeatActionOreo
        @RequiresApi(Build.VERSION_CODES.M)
        get() = Notification.Action.Builder(
            Icon.createWithResource(
                "",
                when {
                    mIsRepeating -> R.drawable.repeat
                    else -> R.drawable.no_repeat
                }
            ),
            ACTION_CHANGE_REPEAT,
            Actions.ChangeRepeat.playbackIntent
        ).build()

    private inline val tenSecsBackActionIfSeekableOreo
        @RequiresApi(Build.VERSION_CODES.M)
        get() = when {
            player.isCurrentMediaItemSeekable -> Notification.Action.Builder(
                Icon.createWithResource("", R.drawable.prev_track),
                ACTION_10_SECS_BACK,
                Actions.TenSecsBack.playbackIntent
            ).build()
            else -> null
        }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getPlayPauseActionOreo(isPlaying: Boolean) = when {
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

    private inline val tenSecsForwardActionIfSeekableOreo
        @RequiresApi(Build.VERSION_CODES.M)
        get() = when {
            player.isCurrentMediaItemSeekable -> Notification.Action.Builder(
                Icon.createWithResource("", R.drawable.next_track),
                ACTION_10_SECS_FORWARD,
                Actions.TenSecsForward.playbackIntent
            ).build()
            else -> null
        }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getLikeActionOreo(isLiked: Boolean) = when {
        isLiked -> Notification.Action.Builder(
            Icon.createWithResource("", R.drawable.like_filled),
            ACTION_REMOVE_FROM_FAVOURITE,
            Actions.RemoveFromFavourite.playbackIntent
        )

        else -> Notification.Action.Builder(
            Icon.createWithResource("", R.drawable.like),
            ACTION_ADD_TO_FAVOURITE,
            Actions.AddToFavourite.playbackIntent
        )
    }.build()

    // --------------------------- Notification Compat ---------------------------

    @kotlin.OptIn(ExperimentalCoroutinesApi::class)
    private val notificationBuilderCompat = currentMetadata.mapLatest {
        NotificationCompat
            .Builder(applicationContext, STREAM_CHANNEL_ID)
            .setContent(currentMetadata = it)
    }

    private fun NotificationCompat.Builder.setContent(currentMetadata: VideoMetadata?) = this
        .setShowWhen(false)
        .setSmallIcon(R.drawable.stream_icon)
        .setAutoCancel(false)
        .setOngoing(true)
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

    private fun NotificationCompat.Builder.setActions(isPlaying: Boolean, isLiked: Boolean) = this
        .clearActions()
        .addAction(repeatActionCompat)
        .also { tenSecsBackActionIfSeekableCompat?.let(it::addAction) }
        .addAction(getPlayPauseActionCompat(isPlaying))
        .also { tenSecsForwardActionIfSeekableCompat?.let(it::addAction) }
        .addAction(getLikeActionCompat(isLiked))

    private inline val repeatActionCompat
        get() = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(
                this,
                when {
                    mIsRepeating -> R.drawable.repeat
                    else -> R.drawable.no_repeat
                }
            ),
            ACTION_CHANGE_REPEAT,
            Actions.ChangeRepeat.playbackIntent
        ).build()

    private inline val tenSecsBackActionIfSeekableCompat
        get() = when {
            player.isCurrentMediaItemSeekable -> NotificationCompat.Action.Builder(
                IconCompat.createWithResource(this, R.drawable.prev_track),
                ACTION_10_SECS_BACK,
                Actions.TenSecsBack.playbackIntent
            ).build()
            else -> null
        }

    private fun getPlayPauseActionCompat(isPlaying: Boolean) = when {
        isPlaying -> NotificationCompat.Action.Builder(
            IconCompat.createWithResource(this, R.drawable.pause),
            ACTION_PAUSE,
            Actions.Pause.playbackIntent
        )

        else -> NotificationCompat.Action.Builder(
            IconCompat.createWithResource(this, R.drawable.play),
            ACTION_RESUME,
            Actions.Resume.playbackIntent
        )
    }.build()

    private inline val tenSecsForwardActionIfSeekableCompat
        get() = when {
            player.isCurrentMediaItemSeekable -> NotificationCompat.Action.Builder(
                IconCompat.createWithResource(this, R.drawable.next_track),
                ACTION_10_SECS_FORWARD,
                Actions.TenSecsForward.playbackIntent
            ).build()
            else -> null
        }

    private fun getLikeActionCompat(isLiked: Boolean) = when {
        isLiked -> NotificationCompat.Action.Builder(
            IconCompat.createWithResource(this, R.drawable.like_filled),
            ACTION_REMOVE_FROM_FAVOURITE,
            Actions.RemoveFromFavourite.playbackIntent
        )

        else -> NotificationCompat.Action.Builder(
            IconCompat.createWithResource(this, R.drawable.like),
            ACTION_ADD_TO_FAVOURITE,
            Actions.AddToFavourite.playbackIntent
        )
    }.build()

    // --------------------------- Handle Notification ---------------------------

    @SuppressLint("NewApi")
    private suspend inline fun showNotification(
        isPlaying: Boolean,
        isLiked: Boolean
    ): Either<Unit, Unit> {
        isNotificationShown = true

        return notificationBuilder.map { oreoBuilder ->
            oreoBuilder.collectLatest { builder ->
                startForeground(
                    NOTIFICATION_ID,
                    builder
                        .setActions(isPlaying, isLiked)
                        .build()
                )
            }
        }.mapLeft { compatBuilder ->
            compatBuilder.collectLatest { builder ->
                startForeground(
                    NOTIFICATION_ID,
                    builder
                        .setLargeIcon(mGetVideoCoverAsync().await())
                        .setActions(isPlaying, isLiked)
                        .build(),
                )
            }
        }
    }

    @SuppressLint("NewApi")
    internal suspend inline fun mUpdateNotification(isPlaying: Boolean, isLiked: Boolean) =
        notificationBuilder.map { oreoBuilder ->
            oreoBuilder.collectLatest { builder ->
                notificationManager.notify(
                    NOTIFICATION_ID,
                    builder
                        .setLargeIcon(mGetVideoCoverAsync().await())
                        .setActions(isPlaying, isLiked)
                        .build()
                )
            }
        }.mapLeft { compatBuilder ->
            compatBuilder.collectLatest { builder ->
                notificationManager.notify(
                    NOTIFICATION_ID,
                    builder
                        .setLargeIcon(mGetVideoCoverAsync().await())
                        .setActions(isPlaying, isLiked)
                        .build()
                )
            }
        }

    internal suspend inline fun mUpdateOrShowNotification(isPlaying: Boolean, isLiked: Boolean) =
        when {
            isNotificationShown -> mUpdateNotification(isPlaying, isLiked)
            else -> showNotification(isPlaying, isLiked)
        }

    internal fun mRemoveNotification() {
        isNotificationShown = false

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> stopForeground(STOP_FOREGROUND_REMOVE)
            else -> stopForeground(true)
        }
    }

    // --------------------------- Playback Monitoring ---------------------------

    internal fun mStartPlaybackMonitoring() {
        playbackMonitorTask = launch {
            while (true) {
                updateAndSendPlaybackPosition()
                delay(PLAYBACK_UPDATE_COOLDOWN)
            }
        }
    }

    internal fun mStopPlaybackMonitoring() = playbackMonitorTask.cancel()
}