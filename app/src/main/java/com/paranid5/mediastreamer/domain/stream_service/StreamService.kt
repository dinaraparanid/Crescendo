package com.paranid5.mediastreamer.domain.stream_service

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.audiofx.Equalizer
import android.media.session.MediaSessionManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.util.SparseArray
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.*
import androidx.media3.common.C.*
import androidx.media3.common.util.NotificationUtil
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerNotificationManager
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.paranid5.mediastreamer.AUDIO_SESSION_ID
import com.paranid5.mediastreamer.EQUALIZER_DATA
import com.paranid5.mediastreamer.IS_PLAYING_STATE
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.data.VideoMetadata
import com.paranid5.mediastreamer.data.utils.extensions.toAndroidMetadata
import com.paranid5.mediastreamer.domain.LifecycleNotificationManager
import com.paranid5.mediastreamer.domain.Receiver
import com.paranid5.mediastreamer.domain.ServiceAction
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.domain.SuspendService
import com.paranid5.mediastreamer.domain.sendBroadcast
import com.paranid5.mediastreamer.domain.utils.extensions.registerReceiverCompat
import com.paranid5.mediastreamer.presentation.main_activity.MainActivity
import com.paranid5.mediastreamer.presentation.streaming.*
import com.paranid5.mediastreamer.presentation.ui.GlideUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

@OptIn(androidx.media3.common.util.UnstableApi::class)
class StreamService : SuspendService(), Receiver, LifecycleNotificationManager, KoinComponent {
    companion object {
        private const val NOTIFICATION_ID = 101
        private const val STREAM_CHANNEL_ID = "stream_channel"
        private const val PLAYBACK_UPDATE_COOLDOWN = 500L
        private const val TEN_SECS_AS_MILLIS = 10000

        private const val SERVICE_LOCATION = "com.paranid5.mediastreamer.domain.stream_service"
        const val Broadcast_PAUSE = "$SERVICE_LOCATION.PAUSE"
        const val Broadcast_RESUME = "$SERVICE_LOCATION.RESUME"
        const val Broadcast_SWITCH_VIDEO = "$SERVICE_LOCATION.SWITCH_VIDEO"
        const val Broadcast_10_SECS_BACK = "$SERVICE_LOCATION.10_SECS_BACK"
        const val Broadcast_10_SECS_FORWARD = "$SERVICE_LOCATION.10_SECS_FORWARD"
        const val Broadcast_SEEK_TO = "$SERVICE_LOCATION.SEEK_TO"
        const val Broadcast_CHANGE_REPEAT = "$SERVICE_LOCATION.CHANGE_REPEAT"
        const val Broadcast_DISMISS_NOTIFICATION = "$SERVICE_LOCATION.DISMISS_NOTIFICATION"

        private const val ACTION_PAUSE = "pause"
        private const val ACTION_RESUME = "resume"
        private const val ACTION_10_SECS_BACK = "back"
        private const val ACTION_10_SECS_FORWARD = "forward"
        private const val ACTION_REPEAT = "repeat"
        private const val ACTION_UNREPEAT = "unrepeat"
        private const val ACTION_DISMISS = "dismiss"

        private val commandsToActions = mapOf(
            ACTION_PAUSE to Actions.Pause,
            ACTION_RESUME to Actions.Resume,
            ACTION_10_SECS_BACK to Actions.TenSecsBack,
            ACTION_10_SECS_FORWARD to Actions.TenSecsForward,
            ACTION_REPEAT to Actions.Repeat,
            ACTION_UNREPEAT to Actions.Unrepeat,
            ACTION_DISMISS to Actions.Dismiss
        )

        const val URL_ARG = "url"
        const val POSITION_ARG = "position"

        private val TAG = StreamService::class.simpleName!!

        internal inline val Intent.mUrlArgOrNull
            get() = getStringExtra(URL_ARG)

        internal inline val Intent.mUrlArg
            get() = mUrlArgOrNull!!
    }

    sealed class Actions(
        override val requestCode: Int,
        override val playbackAction: String
    ) : ServiceAction {
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

        object Repeat : Actions(
            requestCode = NOTIFICATION_ID + 7,
            playbackAction = Broadcast_CHANGE_REPEAT
        )

        object Unrepeat : Actions(
            requestCode = NOTIFICATION_ID + 8,
            playbackAction = Broadcast_CHANGE_REPEAT
        )

        object Dismiss : Actions(
            requestCode = NOTIFICATION_ID + 9,
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

    private val currentMetadataState = MutableStateFlow<VideoMetadata?>(null)
    internal val mIsPlayingState by inject<MutableStateFlow<Boolean>>(named(IS_PLAYING_STATE))

    private val areAudioEffectsEnabledState = storageHandler.areAudioEffectsEnabledState
    private val pitchState = storageHandler.pitchState
    private val speedState = storageHandler.speedState

    private val equalizerState = MutableStateFlow<Equalizer?>(null)
    private val equalizerBandsState = storageHandler.equalizerBandsState
    private val equalizerDataState by inject<MutableStateFlow<EqualizerData?>>(named(EQUALIZER_DATA))

    private lateinit var playbackMonitorTask: Job

    @Volatile
    private var isStoppedWithError = false

    @kotlin.OptIn(ExperimentalCoroutinesApi::class)
    private val videoLength = currentMetadataState
        .mapLatest { it?.lenInMillis ?: 0 }
        .stateIn(scope, SharingStarted.Eagerly, 0)

    internal inline val mCurrentPlaybackPosition
        get() = mPlayer.currentPosition

    internal inline val mIsRepeating
        get() = storageHandler.isRepeatingState.value

    @Volatile
    private var isNotificationShown = false

    // ----------------------- Media Session Management -----------------------

    private lateinit var mediaSessionManager: MediaSessionManager
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var transportControls: MediaControllerCompat.TransportControls

    private val audioSessionIdState by inject<MutableStateFlow<Int>>(named(AUDIO_SESSION_ID))

    internal val mPlayer by lazy {
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
            .apply {
                addListener(playerStateChangedListener)
                audioSessionIdState.update { audioSessionId }

                if (areAudioEffectsEnabledState.value)
                    playbackParameters = PlaybackParameters(speedState.value, pitchState.value)
            }
    }

    internal val mPlayerNotificationManager by lazy {
        PlayerNotificationManager.Builder(this, NOTIFICATION_ID, STREAM_CHANNEL_ID)
            .setChannelNameResourceId(R.string.app_name)
            .setChannelDescriptionResourceId(R.string.app_name)
            .setChannelImportance(NotificationUtil.IMPORTANCE_HIGH)
            .setNotificationListener(notificationListener)
            .setMediaDescriptionAdapter(mediaDescriptionProvider)
            .setCustomActionReceiver(customActionsReceiver)
            .setFastForwardActionIconResourceId(R.drawable.next_track)
            .setRewindActionIconResourceId(R.drawable.prev_track)
            .setPlayActionIconResourceId(R.drawable.play)
            .setPauseActionIconResourceId(R.drawable.pause)
            .build()
            .apply {
                setUseNextAction(false)
                setUsePreviousAction(false)
                setUseStopAction(false)
                setUseChronometer(true)

                setPriority(NotificationCompat.PRIORITY_HIGH)
                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                setMediaSessionToken(mediaSession.sessionToken)
            }
    }

    private val notificationListener = object : PlayerNotificationManager.NotificationListener {
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            super.onNotificationCancelled(notificationId, dismissedByUser)
            detachNotification()
            mPausePlayback()
        }

        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            super.onNotificationPosted(notificationId, notification, ongoing)
            startForeground(notificationId, notification)
        }
    }

    private val mediaDescriptionProvider =
        object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(player: Player) =
                currentMetadataState.value?.title ?: resources.getString(R.string.stream_no_name)

            override fun createCurrentContentIntent(player: Player) = PendingIntent.getActivity(
                applicationContext,
                0,
                Intent(applicationContext, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            override fun getCurrentContentText(player: Player) =
                currentMetadataState.value?.author ?: resources.getString(R.string.unknown_streamer)

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ) = runBlocking { mGetVideoCoverAsync().await() }
        }

    private val customActionsReceiver: PlayerNotificationManager.CustomActionReceiver =
        object : PlayerNotificationManager.CustomActionReceiver {
            override fun createCustomActions(
                context: Context,
                instanceId: Int
            ) = mutableMapOf(
                ACTION_REPEAT to mRepeatActionCompat,
                ACTION_UNREPEAT to mUnrepeatActionCompat,
                ACTION_DISMISS to mDismissNotificationActionCompat
            )

            override fun getCustomActions(player: Player) = mNewCustomActions

            override fun onCustomAction(player: Player, action: String, intent: Intent) =
                sendBroadcast(commandsToActions[action]!!.playbackAction)
        }

    internal val mNewCustomActions
        get() = mutableListOf(if (mIsRepeating) ACTION_REPEAT else ACTION_UNREPEAT, ACTION_DISMISS)

    private inline val isPlaying
        get() = mPlayer.isPlaying

    private val playerStateChangedListener: Player.Listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)

            when (playbackState) {
                Player.STATE_IDLE -> scope.launch { mRestartPlayer() }

                Player.STATE_ENDED -> when {
                    mIsRepeating -> scope.launch { mRestartPlayer() }
                    else -> stopSelf()
                }

                else -> Unit
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)

            mIsPlayingState.update { isPlaying }
            scope.launch { mUpdateNotification() }
            scope.launch { mInitEqualizer() }

            when {
                isPlaying -> mStartPlaybackMonitoring()
                else -> mStopPlaybackMonitoring()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            isStoppedWithError = true
            super.onPlayerError(error)
            Log.e(TAG, "onPlayerError", error)

            Toast.makeText(
                applicationContext,
                "${resources.getString(R.string.error)}: ${error.message ?: resources.getString(R.string.unknown_error)}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // --------------------------- Action Receivers ---------------------------

    private val pauseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "playback paused")
            mPausePlayback()
        }
    }

    private val resumeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "playback resumed")

            when {
                isStoppedWithError -> scope.launch {
                    mRestartPlayer(initialPosition = mCurrentPlaybackPosition)
                    isStoppedWithError = false
                }

                else -> mResumePlayback()
            }
        }
    }

    private val switchVideoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            mSendPlaybackPosition(0)
            val url = intent.mUrlArg
            scope.launch { mStoreCurrentUrl(url) }
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

    private val repeatChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            scope.launch {
                mStoreIsRepeating(!mIsRepeating)
                mPlayerNotificationManager.invalidate()
                Log.d(TAG, "Repeating changed: $mIsRepeating")
            }
        }
    }

    private val dismissNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Notification removed")
            detachNotification()
        }
    }

    override fun registerReceivers() {
        registerReceiverCompat(pauseReceiver, Broadcast_PAUSE)
        registerReceiverCompat(resumeReceiver, Broadcast_RESUME)
        registerReceiverCompat(switchVideoReceiver, Broadcast_SWITCH_VIDEO)
        registerReceiverCompat(tenSecsBackReceiver, Broadcast_10_SECS_BACK)
        registerReceiverCompat(tenSecsForwardReceiver, Broadcast_10_SECS_FORWARD)
        registerReceiverCompat(seekToReceiver, Broadcast_SEEK_TO)
        registerReceiverCompat(repeatChangedReceiver, Broadcast_CHANGE_REPEAT)
        registerReceiverCompat(dismissNotificationReceiver, Broadcast_DISMISS_NOTIFICATION)
    }

    override fun unregisterReceivers() {
        unregisterReceiver(pauseReceiver)
        unregisterReceiver(resumeReceiver)
        unregisterReceiver(switchVideoReceiver)
        unregisterReceiver(tenSecsBackReceiver)
        unregisterReceiver(tenSecsForwardReceiver)
        unregisterReceiver(seekToReceiver)
        unregisterReceiver(repeatChangedReceiver)
        unregisterReceiver(dismissNotificationReceiver)
    }

    // --------------------------- Service Impl ---------------------------

    override fun onCreate() {
        super.onCreate()
        registerReceivers()
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        initMediaSession()

        intent?.mUrlArgOrNull?.let { url ->
            // New stream
            mSendPlaybackPosition(0)
            scope.launch { mStoreCurrentUrl(url) }
            mPlayNewStream(url)
        } ?: scope.launch {
            // Continue with previous stream
            mPlayNewStream(
                url = storageHandler.currentUrlState.value,
                initialPosition = storageHandler.playbackPositionState.value
            )
        }

        launchMonitoringTasks()
        return START_REDELIVER_INTENT
    }

    private fun launchMonitoringTasks() {
        scope.launch { startNotificationObserving() }
        scope.launch { startPlaybackParametersMonitoring() }
        scope.launch { startEqualizerEnabledMonitoring() }
        scope.launch { startEqualizerBandsMonitoring() }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMedia()
        detachNotification()
        unregisterReceivers()
    }

    // ----------------------- Playback Handle -----------------------

    internal fun mSendPlaybackPosition(curPosition: Long = mCurrentPlaybackPosition) =
        sendBroadcast(
            Intent(Broadcast_CUR_POSITION_CHANGED)
                .putExtra(CUR_POSITION_ARG, curPosition)
        )

    private suspend inline fun updateAndSendPlaybackPosition() {
        mSendPlaybackPosition()
        storePlaybackPosition()
    }

    internal fun mPausePlayback() {
        scope.launch { updateAndSendPlaybackPosition() }
        mPlayer.pause()
    }

    internal fun mResumePlayback() {
        mPlayer.playWhenReady = true
    }

    // ----------------------- Storage Handler Utils -----------------------

    private suspend inline fun storePlaybackPosition() =
        storageHandler.storePlaybackPosition(mCurrentPlaybackPosition)

    internal suspend inline fun mStoreIsRepeating(isRepeating: Boolean) =
        storageHandler.storeIsRepeating(isRepeating)

    internal suspend inline fun mStoreCurrentUrl(url: String) =
        storageHandler.storeCurrentUrl(url)

    internal suspend inline fun mStoreMetadata(videoMeta: VideoMeta?) =
        storageHandler.storeCurrentMetadata(videoMeta?.let(::VideoMetadata))

    private suspend inline fun storeCurrentUrl(newUrl: String) =
        storageHandler.storeCurrentUrl(newUrl)

    // ----------------------- Playback Management  -----------------------

    private fun YoutubeUrlExtractor(initialPosition: Long) =
        @SuppressLint("StaticFieldLeak")
        object : YouTubeExtractor(this) {
            override fun onExtractionComplete(
                ytFiles: SparseArray<YtFile>?,
                videoMeta: VideoMeta?
            ) {
                if (ytFiles == null)
                    return

                val audioTag = 140
                val audioUrl = ytFiles[audioTag].url!!

                val videoUrl = sequenceOf(22, 137, 18)
                    .map(ytFiles::get)
                    .filterNotNull()
                    .map(YtFile::getUrl)
                    .filterNotNull()
                    .filter(String::isNotEmpty)
                    .first()

                val audioSource = ProgressiveMediaSource
                    .Factory(DefaultHttpDataSource.Factory())
                    .createMediaSource(MediaItem.fromUri(audioUrl))

                // TODO: video source

                /*val videoSource = ProgressiveMediaSource
                    .Factory(DefaultHttpDataSource.Factory())
                    .createMediaSource(MediaItem.fromUri(videoUrl))*/

                scope.launch {
                    mUpdateMediaSession(videoMeta?.let(::VideoMetadata))
                    mPlayerNotificationManager.invalidate()
                    launch(Dispatchers.IO) { mStoreMetadata(videoMeta) }

                    mPlayer.run {
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
                }
            }
        }

    @OptIn(UnstableApi::class)
    private fun playStream(url: String, initialPosition: Long = 0) =
        YoutubeUrlExtractor(initialPosition).extract(url)

    internal fun mPlayNewStream(url: String, initialPosition: Long = 0) {
        scope.launch { storeCurrentUrl(url) }
        playStream(url, initialPosition)
    }

    internal suspend inline fun mRestartPlayer(initialPosition: Long = 0): Unit =
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            storageHandler.currentUrlState.collectLatest { url -> playStream(url, initialPosition) }
        }

    internal fun mSeekTo(position: Long) =
        mPlayer.seekTo(position)

    internal fun mSeekTo10SecsBack() =
        mPlayer.seekTo(maxOf(mCurrentPlaybackPosition - TEN_SECS_AS_MILLIS, 0))

    internal fun mSeekTo10SecsForward() =
        mPlayer.seekTo(minOf(mCurrentPlaybackPosition + TEN_SECS_AS_MILLIS, videoLength.value))

    private fun releaseMedia() {
        mPlayerNotificationManager.setPlayer(null)
        releaseAudioEffects()
        mPlayer.stop()
        mPlayer.release()
        mediaSession.release()
        transportControls.stop()
    }

    // --------------------------- Playback Monitoring ---------------------------

    internal fun mStartPlaybackMonitoring() {
        playbackMonitorTask = scope.launch {
            while (true) {
                updateAndSendPlaybackPosition()
                delay(PLAYBACK_UPDATE_COOLDOWN)
            }
        }
    }

    internal fun mStopPlaybackMonitoring() = playbackMonitorTask.cancel()

    // --------------------------- Audio Effects ---------------------------

    private suspend inline fun startPlaybackParametersMonitoring() =
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            combine(areAudioEffectsEnabledState, speedState, pitchState) { enabled, speed, pitch ->
                Triple(enabled, speed, pitch)
            }.collectLatest { (enabled, speed, pitch) ->
                mPlayer.playbackParameters = when {
                    enabled -> PlaybackParameters(speed, pitch)
                    else -> PlaybackParameters(1F, 1F)
                }
            }
        }

    internal suspend inline fun mInitEqualizer() = equalizerState.update {
        EqualizerCatching(audioSessionIdState.value).getOrNull()
    }

    private suspend inline fun startEqualizerEnabledMonitoring() =
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            combine(areAudioEffectsEnabledState, audioSessionIdState) { enabled, audioSessionId ->
                enabled to audioSessionId
            }.collectLatest { (enabled, audioSessionId) ->
                equalizerState.value?.release()

                equalizerState.update {
                    if (!enabled) null
                    else EqualizerCatching(audioSessionId).getOrNull()
                }
            }
        }

    private suspend inline fun Equalizer(audioSessionId: Int) = Equalizer(0, audioSessionId).apply {
        if (storageHandler.equalizerBandsState.value == null)
            storageHandler.storeEqualizerBands(
                (0..numberOfBands)
                    .map { getBandLevel(it.toShort()) }
                    .toShortArray()
            )

        equalizerDataState.update {
            EqualizerData(
                minBandLevel = bandLevelRange[0],
                maxBandLevel = bandLevelRange[1],
                bandLevels = equalizerBandsState.value!!
            )
        }

        equalizerBandsState.value?.forEachIndexed { ind, level ->
            setBandLevel(ind.toShort(), level)
        }
    }

    private suspend inline fun EqualizerCatching(audioSessionId: Int) =
        runCatching { Equalizer(audioSessionId) }

    private suspend inline fun startEqualizerBandsMonitoring() =
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            equalizerBandsState.collectLatest { eqBands ->
                equalizerState.update { eq ->
                    if (eq == null || eqBands == null)
                        return@update eq

                    eqBands.forEachIndexed { ind, level ->
                        eq.setBandLevel(ind.toShort(), level)
                    }

                    eq
                }

                equalizerDataState.update { data ->
                    when (val eq = equalizerState.value) {
                        null -> data

                        else -> EqualizerData(
                            minBandLevel = eq.bandLevelRange[0],
                            maxBandLevel = eq.bandLevelRange[1],
                            bandLevels = (0..eq.numberOfBands)
                                .map { eq.getBandLevel(it.toShort()) }
                                .toShortArray()
                        )
                    }
                }
            }
        }

    private fun releaseAudioEffects() {
        equalizerState.update {
            it?.release()
            null
        }

        equalizerDataState.update { null }
    }

    // ----------------------- Media Session Utils -----------------------

    private inline val newMediaSessionCallback
        get() = object : MediaSessionCompat.Callback() {
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
                sendBroadcast(commandsToActions[action]!!.playbackAction)
            }
        }

    private inline val newPlaybackState
        get() = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        or PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PAUSE
                        or PlaybackStateCompat.ACTION_PLAY_PAUSE
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_SEEK_TO
            )
            .setCustomActions()
            .setState(
                if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                mCurrentPlaybackPosition,
                1.0F,
                SystemClock.elapsedRealtime()
            )
            .build()

    private fun PlaybackStateCompat.Builder.setCustomActions() =
        this
            .addCustomAction(
                when {
                    mIsRepeating -> PlaybackStateCompat.CustomAction.Builder(
                        ACTION_REPEAT,
                        resources.getString(R.string.change_repeat),
                        R.drawable.repeat
                    )

                    else -> PlaybackStateCompat.CustomAction.Builder(
                        ACTION_UNREPEAT,
                        resources.getString(R.string.change_repeat),
                        R.drawable.no_repeat
                    )
                }.build()
            )
            .addCustomAction(
                PlaybackStateCompat.CustomAction.Builder(
                    ACTION_DISMISS,
                    resources.getString(R.string.cancel),
                    R.drawable.dismiss
                ).build()
            )

    private fun initMediaSession() {
        mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE)!! as MediaSessionManager
        mediaSession = MediaSessionCompat(applicationContext, TAG)
        transportControls = mediaSession.controller.transportControls

        mediaSession.run {
            isActive = true

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) setFlags(
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                        or MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
            )

            setCallback(newMediaSessionCallback)
            setPlaybackState(newPlaybackState)
        }

        mPlayerNotificationManager.setPlayer(mPlayer)
    }

    internal suspend fun mUpdateMediaSession(
        videoMetadata: VideoMetadata? = currentMetadataState.value
    ) = mediaSession.run {
        setPlaybackState(newPlaybackState)
        setMetadata(
            currentMetadataState
                .updateAndGet { videoMetadata }
                ?.toAndroidMetadata(mGetVideoCoverAsync().await())
        )
    }

    internal suspend inline fun mGetVideoCoverAsync() =
        currentMetadataState
            .value
            ?.let { glideUtils.getVideoCoverBitmapAsync(it) }
            ?: coroutineScope { async(Dispatchers.IO) { glideUtils.thumbnailBitmap } }

    // --------------------------- Notification Actions ---------------------------

    internal inline val mRepeatActionCompat
        get() = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(
                this,
                R.drawable.repeat
            ),
            resources.getString(R.string.change_repeat),
            Actions.Repeat.playbackIntent
        ).build()

    internal inline val mUnrepeatActionCompat
        get() = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(
                this,
                R.drawable.no_repeat
            ),
            resources.getString(R.string.change_repeat),
            Actions.Unrepeat.playbackIntent
        ).build()

    internal inline val mDismissNotificationActionCompat
        get() = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(this, R.drawable.dismiss),
            resources.getString(R.string.cancel),
            Actions.Dismiss.playbackIntent
        ).build()

    // --------------------------- Notification Handle ---------------------------

    /**
     * Runs loop that observers all notification related states
     * and updates notification when something has changed
     */

    override suspend fun startNotificationObserving() =
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            storageHandler.isRepeatingState.collectLatest {
                scope.launch { mUpdateNotification() }
            }
        }

    internal suspend inline fun mUpdateNotification() {
        mUpdateMediaSession()
        mPlayerNotificationManager.invalidate()
    }

    override fun detachNotification() {
        isNotificationShown = false

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> stopForeground(STOP_FOREGROUND_REMOVE)
            else -> stopForeground(true)
        }
    }
}