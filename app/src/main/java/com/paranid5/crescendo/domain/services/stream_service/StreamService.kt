package com.paranid5.crescendo.domain.services.stream_service

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.PresetReverb
import android.media.session.MediaSessionManager
import android.os.Build
import android.os.Bundle
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
import com.paranid5.crescendo.R
import com.paranid5.crescendo.AUDIO_SESSION_ID
import com.paranid5.crescendo.EQUALIZER_DATA
import com.paranid5.crescendo.IS_PLAYING_STATE
import com.paranid5.crescendo.STREAM_SERVICE_CONNECTION
import com.paranid5.crescendo.data.VideoMetadata
import com.paranid5.crescendo.data.eq.EqualizerData
import com.paranid5.crescendo.data.utils.extensions.toAndroidMetadata
import com.paranid5.crescendo.domain.LifecycleNotificationManager
import com.paranid5.crescendo.domain.ReceiverManager
import com.paranid5.crescendo.domain.services.ServiceAction
import com.paranid5.crescendo.domain.StorageHandler
import com.paranid5.crescendo.domain.services.SuspendService
import com.paranid5.crescendo.domain.utils.extensions.bandLevels
import com.paranid5.crescendo.domain.utils.extensions.registerReceiverCompat
import com.paranid5.crescendo.domain.utils.extensions.sendBroadcast
import com.paranid5.crescendo.domain.utils.extensions.setParameter
import com.paranid5.crescendo.presentation.main_activity.MainActivity
import com.paranid5.crescendo.presentation.playing.*
import com.paranid5.crescendo.presentation.ui.utils.GlideUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

@OptIn(androidx.media3.common.util.UnstableApi::class)
class StreamService : SuspendService(), ReceiverManager, LifecycleNotificationManager, KoinComponent {
    companion object {
        private const val NOTIFICATION_ID = 101
        private const val STREAM_CHANNEL_ID = "stream_channel"
        private const val PLAYBACK_UPDATE_COOLDOWN = 500L
        private const val TEN_SECS_AS_MILLIS = 10000

        private const val SERVICE_LOCATION = "com.paranid5.crescendo.domain.services.stream_service"

        const val Broadcast_PAUSE = "$SERVICE_LOCATION.PAUSE"
        const val Broadcast_RESUME = "$SERVICE_LOCATION.RESUME"
        const val Broadcast_SWITCH_VIDEO = "$SERVICE_LOCATION.SWITCH_VIDEO"

        const val Broadcast_10_SECS_BACK = "$SERVICE_LOCATION.10_SECS_BACK"
        const val Broadcast_10_SECS_FORWARD = "$SERVICE_LOCATION.10_SECS_FORWARD"
        const val Broadcast_SEEK_TO = "$SERVICE_LOCATION.SEEK_TO"

        const val Broadcast_CHANGE_REPEAT = "$SERVICE_LOCATION.CHANGE_REPEAT"
        const val Broadcast_DISMISS_NOTIFICATION = "$SERVICE_LOCATION.DISMISS_NOTIFICATION"
        const val Broadcast_STOP = "$SERVICE_LOCATION.STOP"

        const val Broadcast_AUDIO_EFFECTS_ENABLED_UPDATE = "$SERVICE_LOCATION.AUDIO_EFFECTS_ENABLED_UPDATE"
        const val Broadcast_EQUALIZER_PARAM_UPDATE = "$SERVICE_LOCATION.EQUALIZER_PARAM_UPDATE"
        const val Broadcast_BASS_STRENGTH_UPDATE = "$SERVICE_LOCATION.BASS_STRENGTH_UPDATE"
        const val Broadcast_REVERB_PRESET_UPDATE = "$SERVICE_LOCATION.REVERB_PRESET_UPDATE"

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

        internal fun mGetRepeatMode(isRepeating: Boolean) = when {
            isRepeating -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_ALL
        }
    }

    sealed class Actions(
        override val requestCode: Int,
        override val playbackAction: String
    ) : ServiceAction {
        data object Pause : Actions(
            requestCode = NOTIFICATION_ID + 1,
            playbackAction = Broadcast_PAUSE
        )

        data object Resume : Actions(
            requestCode = NOTIFICATION_ID + 2,
            playbackAction = Broadcast_RESUME
        )

        data object TenSecsBack : Actions(
            requestCode = NOTIFICATION_ID + 3,
            playbackAction = Broadcast_10_SECS_BACK
        )

        data object TenSecsForward : Actions(
            requestCode = NOTIFICATION_ID + 4,
            playbackAction = Broadcast_10_SECS_FORWARD
        )

        data object Repeat : Actions(
            requestCode = NOTIFICATION_ID + 7,
            playbackAction = Broadcast_CHANGE_REPEAT
        )

        data object Unrepeat : Actions(
            requestCode = NOTIFICATION_ID + 8,
            playbackAction = Broadcast_CHANGE_REPEAT
        )

        data object Dismiss : Actions(
            requestCode = NOTIFICATION_ID + 9,
            playbackAction = Broadcast_DISMISS_NOTIFICATION
        )
    }

    private inline val Actions.playbackIntent: PendingIntent
        get() = PendingIntent.getBroadcast(
            this@StreamService,
            requestCode,
            Intent(playbackAction),
            PendingIntent.FLAG_IMMUTABLE
        )

    private val storageHandler by inject<StorageHandler>()
    private val glideUtils by inject<GlideUtils> { parametersOf(this) }

    private val currentUrlState = storageHandler.currentUrlState
    private val currentMetadataState = MutableStateFlow<VideoMetadata?>(null)

    private val playbackPositionState = storageHandler.playbackPositionState
    internal val mIsRepeatingState = storageHandler.isRepeatingState
    internal val mIsPlayingState by inject<MutableStateFlow<Boolean>>(named(IS_PLAYING_STATE))

    private val areAudioEffectsEnabledState = storageHandler.areAudioEffectsEnabledState
    private val pitchState = storageHandler.pitchState
    private val speedState = storageHandler.speedState

    private lateinit var equalizer: Equalizer
    private lateinit var bassBoost: BassBoost
    private lateinit var reverb: PresetReverb

    private val equalizerParamState = storageHandler.equalizerParamState
    private val equalizerBandsState = storageHandler.equalizerBandsState
    private val equalizerPresetState = storageHandler.equalizerPresetState

    private val bassStrengthState = storageHandler.bassStrengthState
    private val reverbPresetState = storageHandler.reverbPresetState

    internal val mEqualizerDataState by inject<MutableStateFlow<EqualizerData?>>(
        named(EQUALIZER_DATA)
    )

    private val isConnectedState by inject<MutableStateFlow<Boolean>>(
        named(STREAM_SERVICE_CONNECTION)
    )

    private val startIdState = MutableStateFlow(0)

    private lateinit var playbackTask: Job
    private lateinit var playbackPosMonitorTask: Job

    @Volatile
    private var isStoppedWithError = false

    @kotlin.OptIn(ExperimentalCoroutinesApi::class)
    private val videoLength = currentMetadataState
        .mapLatest { it?.lenInMillis ?: 0 }
        .stateIn(scope, SharingStarted.Eagerly, 0)

    internal inline val mCurrentPlaybackPosition
        get() = mPlayer.currentPosition

    @Volatile
    private var isNotificationShown = false

    // ----------------------- Media Session Management -----------------------

    private lateinit var mediaSessionManager: MediaSessionManager
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var transportControls: MediaControllerCompat.TransportControls

    private val audioSessionIdState by inject<MutableStateFlow<Int>>(named(AUDIO_SESSION_ID))

    internal val mPlayer by lazy {
        ExoPlayer.Builder(applicationContext)
            .setAudioAttributes(newAudioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(WAKE_MODE_NETWORK)
            .build()
            .apply {
                addListener(playerStateChangedListener)
                audioSessionIdState.update { audioSessionId }
                repeatMode = mGetRepeatMode(isRepeating = mIsRepeatingState.value)
                initAudioEffects()
            }
    }

    private inline val newAudioAttributes
        get() = AudioAttributes.Builder()
            .setContentType(AUDIO_CONTENT_TYPE_MOVIE)
            .setUsage(USAGE_MEDIA)
            .build()

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
                setUseStopAction(false)
                setUseChronometer(false)
                setUseNextAction(false)
                setUsePreviousAction(false)
                setUseNextActionInCompactView(false)
                setUsePreviousActionInCompactView(false)

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
                currentMetadataState.value?.title ?: getString(R.string.stream_no_name)

            override fun createCurrentContentIntent(player: Player) = PendingIntent.getActivity(
                applicationContext,
                0,
                Intent(applicationContext, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            override fun getCurrentContentText(player: Player) =
                currentMetadataState.value?.author ?: getString(R.string.unknown_streamer)

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
        get() = mutableListOf(
            when {
                mIsRepeatingState.value -> ACTION_REPEAT
                else -> ACTION_UNREPEAT
            },
            ACTION_DISMISS
        )

    private inline val isPlaying
        get() = mPlayer.isPlaying

    private val playerStateChangedListener: Player.Listener = object : Player.Listener {
        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)

            if (reason == Player.DISCONTINUITY_REASON_AUTO_TRANSITION) scope.launch {
                mSendAndStorePlaybackPosition()
                mUpdateNotification()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)

            when (playbackState) {
                Player.STATE_IDLE -> scope.launch {
                    mRestartPlayer(initialPosition = mCurrentPlaybackPosition)
                }

                else -> Unit
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            mIsPlayingState.update { isPlaying }

            when {
                isPlaying -> mStartPlaybackPositionMonitoring()
                else -> mStopPlaybackPositionMonitoring()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            isStoppedWithError = true
            super.onPlayerError(error)
            Log.e(TAG, "onPlayerError", error)

            Toast.makeText(
                applicationContext,
                "${getString(R.string.error)}: ${error.message ?: getString(R.string.unknown_error)}",
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
            mStoreAndPlayNewStream(url)
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
            mResetAudioSessionId()
            mSeekTo(position)
        }
    }

    private val repeatChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            scope.launch {
                val newRepeatMode = !mIsRepeatingState.value
                mStoreIsRepeating(newRepeatMode)
                mPlayer.repeatMode = mGetRepeatMode(newRepeatMode)
                mPlayerNotificationManager.invalidate()
                Log.d(TAG, "Repeating changed: $newRepeatMode")
            }
        }
    }

    private val dismissNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Notification removed")
            detachNotification()
        }
    }

    private val audioEffectsEnabledUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val isEnabled = areAudioEffectsEnabledState.value
            mSetAudioEffectsEnabled(isEnabled)
        }
    }

    private val equalizerParameterUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val currentParameter = equalizerParamState.value
            val bandLevels = equalizerBandsState.value
            val preset = equalizerPresetState.value

            equalizer.setParameter(currentParameter, bandLevels, preset)
            Log.d(TAG, "EQ Params Set: $currentParameter; EQ: $bandLevels")

            mEqualizerDataState.update {
                EqualizerData(
                    eq = equalizer,
                    bandLevels = bandLevels,
                    currentPreset = preset,
                    currentParameter = currentParameter
                )
            }
        }
    }

    private val bassStrengthUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            bassBoost.setStrength(bassStrengthState.value)
        }
    }

    private val reverbPresetUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            reverb.preset = reverbPresetState.value
        }
    }

    private val stopReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Stopped after stop receive: ${stopSelfResult(startIdState.value)}")
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
        registerReceiverCompat(audioEffectsEnabledUpdateReceiver, Broadcast_AUDIO_EFFECTS_ENABLED_UPDATE)
        registerReceiverCompat(equalizerParameterUpdateReceiver, Broadcast_EQUALIZER_PARAM_UPDATE)
        registerReceiverCompat(bassStrengthUpdateReceiver, Broadcast_BASS_STRENGTH_UPDATE)
        registerReceiverCompat(reverbPresetUpdateReceiver, Broadcast_REVERB_PRESET_UPDATE)
        registerReceiverCompat(stopReceiver, Broadcast_STOP)
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
        unregisterReceiver(audioEffectsEnabledUpdateReceiver)
        unregisterReceiver(equalizerParameterUpdateReceiver)
        unregisterReceiver(bassStrengthUpdateReceiver)
        unregisterReceiver(reverbPresetUpdateReceiver)
        unregisterReceiver(stopReceiver)
    }

    // --------------------------- Service Impl ---------------------------

    override fun onCreate() {
        super.onCreate()
        registerReceivers()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "onStart called with id $startId")

        isConnectedState.update { true }
        startIdState.update { startId }
        initMediaSession()

        scope.launch {
            when (val url = intent?.mUrlArgOrNull) {
                // Continue with previous stream
                null -> onResumeClicked()

                // New stream
                else -> onFetchVideoClicked(url)
            }

            launchMonitoringTasks()
        }

        return START_STICKY
    }

    private fun onResumeClicked() = mStoreAndPlayNewStream(
        url = currentUrlState.value,
        initialPosition = playbackPositionState.value
    )

    private suspend inline fun onFetchVideoClicked(url: String) {
        mSendPlaybackPosition(0)
        mStoreCurrentUrl(url)
        mStoreAndPlayNewStream(url)    }

    private fun launchMonitoringTasks() {
        scope.launch { startNotificationObserving() }
        scope.launch { startAudioEffectsMonitoring() }
    }

    override fun onDestroy() {
        super.onDestroy()
        isConnectedState.update { false }
        playbackTask.cancel()
        detachNotification()
        releaseMedia()
        unregisterReceivers()
    }

    // ----------------------- Playback Handle -----------------------

    internal fun mSendPlaybackPosition(curPosition: Long = mCurrentPlaybackPosition) =
        sendBroadcast(
            Intent(Broadcast_CUR_POSITION_CHANGED)
                .putExtra(CUR_POSITION_ARG, curPosition)
        )

    internal suspend inline fun mSendAndStorePlaybackPosition() {
        mSendPlaybackPosition()
        storePlaybackPosition()
    }

    internal fun mPausePlayback() {
        scope.launch { mSendAndStorePlaybackPosition() }
        mPlayer.pause()
        mSetAudioEffectsEnabled(isEnabled = false)
    }

    internal fun mResumePlayback() {
        mResetAudioSessionId()
        mPlayer.playWhenReady = true
        mSetAudioEffectsEnabled(isEnabled = areAudioEffectsEnabledState.value)
    }

    private fun ExoPlayer.initAudioEffects() {
        initEqualizer(audioSessionId)
        initBassBoost(audioSessionId)
        initReverb(audioSessionId)

        if (areAudioEffectsEnabledState.value)
            playbackParameters = PlaybackParameters(speedState.value, pitchState.value)
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

    internal fun mResetAudioSessionId() {
        audioSessionIdState.update { mPlayer.audioSessionId }
        mPlayer.initAudioEffects()
    }

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

                val audioSource = ProgressiveMediaSource
                    .Factory(DefaultHttpDataSource.Factory())
                    .createMediaSource(MediaItem.fromUri(audioUrl))

                playbackTask = scope.launch {
                    mUpdateMediaSession(videoMeta?.let(::VideoMetadata))
                    mPlayerNotificationManager.invalidate()
                    launch(Dispatchers.IO) { mStoreMetadata(videoMeta) }

                    mPlayer.run {
                        setMediaSource(audioSource)
                        playWhenReady = true
                        prepare()
                        seekTo(initialPosition)
                    }

                    mSetAudioEffectsEnabled(isEnabled = areAudioEffectsEnabledState.value)
                }
            }
        }

    @OptIn(UnstableApi::class)
    private fun playStream(url: String, initialPosition: Long = 0) {
        mResetAudioSessionId()
        YoutubeUrlExtractor(initialPosition).extract(url)
    }

    internal fun mStoreAndPlayNewStream(url: String, initialPosition: Long = 0) {
        scope.launch { storeCurrentUrl(url) }
        playStream(url, initialPosition)
    }

    internal fun mRestartPlayer(initialPosition: Long = 0) =
        playStream(url = currentUrlState.value, initialPosition)

    internal fun mSeekTo(position: Long) {
        mResetAudioSessionId()
        mPlayer.seekTo(position)
    }

    internal fun mSeekTo10SecsBack() {
        mResetAudioSessionId()
        mPlayer.seekTo(maxOf(mCurrentPlaybackPosition - TEN_SECS_AS_MILLIS, 0))
    }

    internal fun mSeekTo10SecsForward() {
        mResetAudioSessionId()
        mPlayer.seekTo(minOf(mCurrentPlaybackPosition + TEN_SECS_AS_MILLIS, videoLength.value))
    }

    /**
     * Releases all media related handlers, such as
     * [mPlayer], audio effects, [mediaSession] and [transportControls].
     * Additionally, resets [audioSessionIdState] with zero
     */

    private fun releaseMedia() {
        mPlayerNotificationManager.setPlayer(null)
        releaseAudioEffects()
        mPlayer.stop()
        mPlayer.release()
        mediaSession.release()
        transportControls.stop()
        audioSessionIdState.update { 0 }
    }

    // --------------------------- Playback Monitoring ---------------------------

    internal fun mStartPlaybackPositionMonitoring() {
        playbackPosMonitorTask = scope.launch {
            while (true) {
                mSendAndStorePlaybackPosition()
                delay(PLAYBACK_UPDATE_COOLDOWN)
            }
        }
    }

    internal fun mStopPlaybackPositionMonitoring() = playbackPosMonitorTask.cancel()

    // --------------------------- Audio Effects ---------------------------

    private suspend inline fun startAudioEffectsMonitoring() =
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            combine(
                areAudioEffectsEnabledState,
                speedState,
                pitchState
            ) { enabled, speed, pitch ->
                Triple(enabled, speed, pitch)
            }.collectLatest { (enabled, speed, pitch) ->
                mPlayer.playbackParameters = when {
                    enabled -> PlaybackParameters(speed, pitch)
                    else -> PlaybackParameters(1F, 1F)
                }

                mUpdateNotification()
            }
        }

    private fun initEqualizer(audioSessionId: Int) {
        equalizer = Equalizer(0, audioSessionId).apply {
            val data = mEqualizerDataState.updateAndGet {
                EqualizerData(
                    eq = this,
                    bandLevels = equalizerBandsState.value,
                    currentPreset = equalizerPresetState.value,
                    currentParameter = equalizerParamState.value
                )
            }!!

            setParameter(
                currentParameter = data.currentParameter,
                bandLevels = data.bandLevels,
                preset = data.currentPreset
            )

            Log.d(TAG, "EQ Params Set: $data; EQ: $bandLevels")
        }
    }

    private fun initBassBoost(audioSessionId: Int) {
        bassBoost = BassBoost(0, audioSessionId).apply {
            try {
                setStrength(bassStrengthState.value)
            } catch (ignored: IllegalArgumentException) {
                // Invalid strength
            }
        }
    }

    private fun initReverb(audioSessionId: Int) {
        reverb = PresetReverb(0, audioSessionId).apply {
            try {
                preset = reverbPresetState.value
            } catch (ignored: IllegalArgumentException) {
                // Invalid preset
            }
        }
    }

    internal fun mSetAudioEffectsEnabled(isEnabled: Boolean) {
        mPlayer.playbackParameters = when {
            isEnabled -> PlaybackParameters(speedState.value, pitchState.value)
            else -> PlaybackParameters(1F, 1F)
        }

        // For some reason, it requires multiple tries to enable...
        repeat(3) {
            try {
                equalizer.enabled = isEnabled
                bassBoost.enabled = isEnabled
                reverb.enabled = isEnabled
            } catch (ignored: IllegalStateException) {
                // not initialized
            }
        }
    }

    private fun releaseAudioEffects() {
        mSetAudioEffectsEnabled(isEnabled = false)
        equalizer.release()
        bassBoost.release()
        reverb.release()
        mEqualizerDataState.update { null }
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
                speedState.value,
                SystemClock.elapsedRealtime()
            )
            .build()

    private fun PlaybackStateCompat.Builder.setCustomActions() =
        this
            .addCustomAction(
                when {
                    mIsRepeatingState.value -> PlaybackStateCompat.CustomAction.Builder(
                        ACTION_REPEAT,
                        getString(R.string.change_repeat),
                        R.drawable.repeat
                    )

                    else -> PlaybackStateCompat.CustomAction.Builder(
                        ACTION_UNREPEAT,
                        getString(R.string.change_repeat),
                        R.drawable.no_repeat
                    )
                }.build()
            )
            .addCustomAction(
                PlaybackStateCompat.CustomAction.Builder(
                    ACTION_DISMISS,
                    getString(R.string.cancel),
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

    internal suspend inline fun mUpdateMediaSession(
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
            getString(R.string.change_repeat),
            Actions.Repeat.playbackIntent
        ).build()

    internal inline val mUnrepeatActionCompat
        get() = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(
                this,
                R.drawable.no_repeat
            ),
            getString(R.string.change_repeat),
            Actions.Unrepeat.playbackIntent
        ).build()

    internal inline val mDismissNotificationActionCompat
        get() = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(this, R.drawable.dismiss),
            getString(R.string.cancel),
            Actions.Dismiss.playbackIntent
        ).build()

    // --------------------------- Notification Handle ---------------------------

    /**
     * Runs loop that observers all notification related states
     * and updates notification when something has changed
     */

    override suspend fun startNotificationObserving() =
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            combine(mIsPlayingState, mIsRepeatingState) { isPlaying, isRepeating ->
                isPlaying to isRepeating
            }.collectLatest {
                scope.launch { mUpdateNotification() }
            }
        }

    internal suspend inline fun mUpdateNotification() {
        mUpdateMediaSession()
        mPlayerNotificationManager.invalidate()
    }

    override fun detachNotification() {
        Log.d(TAG, "Notification is remove")
        isNotificationShown = false

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> stopForeground(STOP_FOREGROUND_REMOVE)
            else -> stopForeground(true)
        }
    }
}