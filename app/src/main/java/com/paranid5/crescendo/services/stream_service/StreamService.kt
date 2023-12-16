package com.paranid5.crescendo.services.stream_service

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.NotificationUtil
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import com.paranid5.crescendo.R
import com.paranid5.crescendo.STREAM_SERVICE_CONNECTION
import com.paranid5.crescendo.domain.VideoMetadata
import com.paranid5.crescendo.domain.utils.extensions.registerReceiverCompat
import com.paranid5.crescendo.domain.utils.extensions.sendBroadcast
import com.paranid5.crescendo.domain.utils.extensions.toAndroidMetadata
import com.paranid5.crescendo.services.SuspendService
import com.paranid5.crescendo.services.service_controllers.MediaRetrieverController
import com.paranid5.crescendo.services.service_controllers.MediaSessionController
import com.paranid5.crescendo.services.service_controllers.PlaybackController
import com.paranid5.crescendo.presentation.main.MainActivity
import com.paranid5.crescendo.presentation.main.playing.Broadcast_CUR_POSITION_CHANGED
import com.paranid5.crescendo.presentation.main.playing.CUR_POSITION_STREAMING_ARG
import com.paranid5.yt_url_extractor_kt.VideoMeta
import com.paranid5.yt_url_extractor_kt.YtFailure
import com.paranid5.yt_url_extractor_kt.YtFilesNotFoundException
import com.paranid5.yt_url_extractor_kt.YtRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

@OptIn(UnstableApi::class)
class StreamService : SuspendService(), KoinComponent {
    @Suppress("IncorrectFormatting")
    companion object {
        private val TAG = StreamService::class.simpleName!!

        private const val NOTIFICATION_ID = 101
        private const val STREAM_CHANNEL_ID = "stream_channel"
        private const val PLAYBACK_UPDATE_COOLDOWN = 500L

        private const val SERVICE_LOCATION = "com.paranid5.crescendo.services.stream_service"

        const val Broadcast_PAUSE = "$SERVICE_LOCATION.PAUSE"
        const val Broadcast_RESUME = "$SERVICE_LOCATION.RESUME"
        const val Broadcast_SWITCH_VIDEO = "$SERVICE_LOCATION.SWITCH_VIDEO"

        const val Broadcast_10_SECS_BACK = "$SERVICE_LOCATION.10_SECS_BACK"
        const val Broadcast_10_SECS_FORWARD = "$SERVICE_LOCATION.10_SECS_FORWARD"
        const val Broadcast_SEEK_TO = "$SERVICE_LOCATION.SEEK_TO"

        const val Broadcast_CHANGE_REPEAT = "$SERVICE_LOCATION.CHANGE_REPEAT"
        const val Broadcast_DISMISS_NOTIFICATION = "$SERVICE_LOCATION.DISMISS_NOTIFICATION"

        const val Broadcast_AUDIO_EFFECTS_ENABLED_UPDATE = "$SERVICE_LOCATION.AUDIO_EFFECTS_ENABLED_UPDATE"
        const val Broadcast_EQUALIZER_PARAM_UPDATE = "$SERVICE_LOCATION.EQUALIZER_PARAM_UPDATE"
        const val Broadcast_BASS_STRENGTH_UPDATE = "$SERVICE_LOCATION.BASS_STRENGTH_UPDATE"
        const val Broadcast_REVERB_PRESET_UPDATE = "$SERVICE_LOCATION.REVERB_PRESET_UPDATE"
        const val Broadcast_STOP = "$SERVICE_LOCATION.STOP"

        private const val ACTION_PAUSE = "pause"
        private const val ACTION_RESUME = "resume"
        private const val ACTION_10_SECS_BACK = "back"
        private const val ACTION_10_SECS_FORWARD = "forward"
        private const val ACTION_REPEAT = "repeat"
        private const val ACTION_UNREPEAT = "unrepeat"
        private const val ACTION_DISMISS = "dismiss"

        const val URL_ARG = "url"
        const val POSITION_ARG = "position"

        private const val DEFAULT_AUDIO_TAG = 140

        private inline val Intent.urlArgOrNull
            get() = getStringExtra(URL_ARG)

        private inline val Intent.urlArg
            get() = urlArgOrNull!!
    }

    private val currentMetadataState = MutableStateFlow<VideoMetadata?>(null)

    private val isConnectedState by inject<MutableStateFlow<Boolean>>(
        named(STREAM_SERVICE_CONNECTION)
    )

    // ----------------------- Media Session Management -----------------------

    private val mediaRetrieverController by lazy {
        MediaRetrieverController(context = this)
    }

    private val mediaSessionController by lazy {
        MediaSessionController(context = this, tag = TAG)
    }

    private inline val mediaSession
        get() = mediaSessionController.mediaSession

    private inline val newMediaSessionCallback
        get() = object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                playbackController.resumePlayback()
            }

            override fun onPause() {
                super.onPause()
                pausePlayback()
            }

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
                playbackController.seekTo(pos)
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                playbackController.seekTo10SecsForward(videoLengthState.value)
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                playbackController.seekTo10SecsBack()
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
                when {
                    mediaRetrieverController.isPlaying -> PlaybackStateCompat.STATE_PLAYING
                    else -> PlaybackStateCompat.STATE_PAUSED
                },
                currentPlaybackPosition,
                mediaRetrieverController.speed,
                SystemClock.elapsedRealtime()
            )
            .build()

    private fun PlaybackStateCompat.Builder.setCustomActions(): PlaybackStateCompat.Builder {
        val repeatAction = when {
            mediaRetrieverController.isRepeating -> PlaybackStateCompat.CustomAction.Builder(
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

        val cancelAction = PlaybackStateCompat.CustomAction.Builder(
            ACTION_DISMISS,
            getString(R.string.cancel),
            R.drawable.dismiss
        ).build()

        return this
            .addCustomAction(repeatAction)
            .addCustomAction(cancelAction)
    }

    private fun initMediaSession() {
        mediaSessionController.initMediaSession(
            mediaSessionCallback = newMediaSessionCallback,
            playbackState = newPlaybackState
        )

        playerNotificationManager.setPlayer(playbackController.player)
    }

    private suspend fun updateMediaSession(
        videoMetadata: VideoMetadata? = currentMetadataState.value
    ) = mediaSessionController.updateMediaSession(
        playbackState = newPlaybackState,
        metadata = currentMetadataState
            .updateAndGet { videoMetadata }
            ?.toAndroidMetadata(getVideoCoverAsync().await())
    )

    private suspend inline fun getVideoCoverAsync() =
        currentMetadataState
            .value
            ?.let { mediaRetrieverController.getVideoCoverBitmapAsync(it) }
            ?: coroutineScope {
                async(Dispatchers.IO) {
                    mediaRetrieverController.getThumbnailBitmap()
                }
            }

    // ----------------------- Player Management -----------------------

    @Volatile
    private var isStoppedWithError = false

    private val playbackController by lazy {
        PlaybackController(
            context = this,
            playerStateChangedListener = playerStateChangedListener,
            mediaRetrieverController = mediaRetrieverController,
            playbackType = PlaybackController.PlaybackType.STREAM
        )
    }

    private inline val playerStateChangedListener: Player.Listener
        get() = object : Player.Listener {
            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                super.onPositionDiscontinuity(oldPosition, newPosition, reason)

                if (reason == Player.DISCONTINUITY_REASON_AUTO_TRANSITION)
                    transitToNextTrackAsync()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)

                when (playbackState) {
                    Player.STATE_IDLE -> restartPlayer()
                    Player.STATE_BUFFERING -> playbackController.seekToNextMediaItem()
                    else -> Unit
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                mediaRetrieverController.setPlaying(isPlaying)

                when {
                    isPlaying -> startPlaybackPositionMonitoring()
                    else -> stopPlaybackPositionMonitoring()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                isStoppedWithError = true
                super.onPlayerError(error)
                Log.e(TAG, "onPlayerError", error)
                sendErrorBroadcast(error)
            }

            private fun transitToNextTrackAsync() = scope.launch {
                sendAndStorePlaybackPosition()
                updateNotification()
            }
        }

    // ----------------------- Playback Handle -----------------------

    private var playbackTask: Job? = null

    private inline val currentPlaybackPosition
        get() = playbackController.currentPosition

    @kotlin.OptIn(ExperimentalCoroutinesApi::class)
    private val videoLengthState = currentMetadataState
        .mapLatest { it?.lenInMillis ?: 0 }
        .stateIn(scope, SharingStarted.Eagerly, 0)

    private fun sendPlaybackPosition(curPosition: Long = currentPlaybackPosition) =
        sendBroadcast(
            Intent(Broadcast_CUR_POSITION_CHANGED)
                .putExtra(CUR_POSITION_STREAMING_ARG, curPosition)
        )

    private suspend fun sendAndStorePlaybackPosition() {
        sendPlaybackPosition()
        storePlaybackPosition()
    }

    private suspend fun extractAudioUrlWithMeta(ytUrl: String): Result<Pair<String, VideoMeta?>> {
        val extractRes = withTimeoutOrNull(timeMillis = 4500) {
            mediaRetrieverController.extractYtFilesWithMeta(
                context = applicationContext,
                ytUrl = ytUrl
            )
        } ?: return YtFailure(YtRequestTimeoutException())

        val (ytFiles, liveStreamManifestsRes, videoMetaRes) =
            when (val res = extractRes.getOrNull()) {
                null -> return Result.failure(extractRes.exceptionOrNull()!!)
                else -> res
            }

        val videoMeta = videoMetaRes.getOrNull()
        val liveStreamManifests = liveStreamManifestsRes.getOrNull()

        val audioUrl = when (videoMeta?.isLiveStream) {
            true -> liveStreamManifests?.hlsManifestUrl
            else -> ytFiles[DEFAULT_AUDIO_TAG]?.url
        }

        return when (audioUrl) {
            null -> Result.failure(YtFilesNotFoundException())
            else -> Result.success(audioUrl to videoMeta)
        }
    }

    private suspend fun extractMediaFilesAndStartPlaying(
        ytUrl: String,
        initialPosition: Long
    ) {
        val extractRes = extractAudioUrlWithMeta(ytUrl)
        if (extractRes.isFailure) return sendErrorBroadcast(extractRes.exceptionOrNull()!!)

        val (audioUrl, videoMeta) = extractRes.getOrNull()!!

        Log.d(TAG, "Url: $audioUrl")
        Log.d(TAG, "Position: $initialPosition")

        playbackTask = scope.launch {
            updateMediaSession(videoMeta?.let(::VideoMetadata))
            playerNotificationManager.invalidate()
            launch(Dispatchers.IO) { storeMetadata(videoMeta) }

            playbackController.player.run {
                setMediaItem(MediaItem.fromUri(audioUrl))
                playWhenReady = true
                prepare()
                seekTo(initialPosition)
            }

            playbackController.setAudioEffectsEnabled(
                isEnabled = mediaRetrieverController.areAudioEffectsEnabled,
                mediaRetrieverController = mediaRetrieverController
            )
        }
    }

    @OptIn(UnstableApi::class)
    private fun playStream(ytUrl: String, initialPosition: Long = 0) {
        playbackController.resetAudioSessionIdIfNotPlaying()

        scope.launch(Dispatchers.IO) {
            extractMediaFilesAndStartPlaying(
                ytUrl = ytUrl,
                initialPosition = initialPosition
            )
        }
    }

    private fun storeAndPlayNewStream(url: String, initialPosition: Long = 0) {
        scope.launch { storeCurrentUrl(url) }
        playStream(url, initialPosition)
    }

    private fun restartPlayer() = playStream(
        ytUrl = mediaRetrieverController.currentUrl,
        initialPosition = mediaRetrieverController.streamPlaybackPosition
    )

    private fun releaseMedia() {
        playerNotificationManager.setPlayer(null)
        playbackController.releaseAudioEffects()
        playbackController.releasePlayer()
        mediaSessionController.releaseMediaSession()
    }

    private fun pausePlayback() {
        scope.launch { sendAndStorePlaybackPosition() }
        playbackController.pause()
    }

    // --------------------------- Playback Monitoring ---------------------------

    private var playbackPosMonitorTask: Job? = null

    private fun startPlaybackPositionMonitoring() {
        playbackPosMonitorTask = scope.launch {
            while (true) {
                sendAndStorePlaybackPosition()
                delay(PLAYBACK_UPDATE_COOLDOWN)
            }
        }
    }

    private fun stopPlaybackPositionMonitoring() = playbackPosMonitorTask?.cancel()

    private suspend inline fun startAudioEffectsMonitoring() =
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            combine(
                mediaRetrieverController.areAudioEffectsEnabledState,
                mediaRetrieverController.speedState,
                mediaRetrieverController.pitchState
            ) { enabled, speed, pitch ->
                Triple(enabled, speed, pitch)
            }.collectLatest { (enabled, speed, pitch) ->
                playbackController.playbackParameters = when {
                    enabled -> PlaybackParameters(speed, pitch)
                    else -> PlaybackParameters(1F, 1F)
                }

                updateNotification()
            }
        }

    // --------------------------- Broadcast Receivers ---------------------------

    private val pauseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "playback paused")
            pausePlayback()
        }
    }

    private val resumeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "playback resumed")

            when {
                isStoppedWithError -> {
                    restartPlayer()
                    isStoppedWithError = false
                }

                else -> playbackController.resumePlayback()
            }
        }
    }

    private val switchVideoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            sendPlaybackPosition(0)
            val url = intent.urlArg
            scope.launch { storeCurrentUrl(url) }
            storeAndPlayNewStream(url)
        }
    }

    private val tenSecsBackReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "10 secs back")
            playbackController.seekTo10SecsBack()
        }
    }

    private val tenSecsForwardReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "10 secs forward")
            playbackController.seekTo10SecsForward(videoLengthState.value)
        }
    }

    private val seekToReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val position = intent.getLongExtra(POSITION_ARG, 0)
            Log.d(TAG, "seek to $position")
            playbackController.resetAudioSessionIdIfNotPlaying()
            playbackController.seekTo(position)
        }
    }

    private val repeatChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            scope.launch {
                val newRepeatMode = !mediaRetrieverController.isRepeating
                storeIsRepeating(newRepeatMode)
                playbackController.repeatMode = playbackController.getRepeatMode(newRepeatMode)
                playerNotificationManager.invalidate()
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
        override fun onReceive(context: Context?, intent: Intent?) =
            playbackController.setAudioEffectsEnabled(
                isEnabled = mediaRetrieverController.areAudioEffectsEnabled,
                mediaRetrieverController = mediaRetrieverController
            )
    }

    private val equalizerParameterUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val currentParameter = mediaRetrieverController.equalizerParams
            val bandLevels = mediaRetrieverController.equalizerBands
            val preset = mediaRetrieverController.equalizerPreset

            playbackController.setEqParameter(currentParameter, bandLevels, preset)
            Log.d(TAG, "EQ Params Set: $currentParameter; EQ: $bandLevels")

            playbackController.updateEqData(
                bandLevels = bandLevels,
                currentPreset = preset,
                currentParameter = currentParameter
            )
        }
    }

    private val bassStrengthUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            playbackController.bassStrength = mediaRetrieverController.bassStrength
        }
    }

    private val reverbPresetUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            playbackController.reverbPreset = mediaRetrieverController.reverbPreset
        }
    }

    private val stopReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Stopped after stop receive: ${stopSelfResult(startIdState.value)}")
        }
    }

    @Suppress("IncorrectFormatting")
    private fun registerReceivers() {
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

    private fun unregisterReceivers() {
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

    private val startIdState = MutableStateFlow(0)

    override fun onCreate() {
        super.onCreate()
        registerReceivers()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startIdState.update { startId }
        Log.d(TAG, "onStart called with id $startId")

        isConnectedState.update { true }
        initMediaSession()

        scope.launch {
            when (val url = intent?.urlArgOrNull) {
                // Continue with previous stream
                null -> onResumeClicked()

                // New stream
                else -> onFetchVideoClicked(url)
            }

            launchMonitoringTasks()
        }

        return START_STICKY
    }

    private fun onResumeClicked() = storeAndPlayNewStream(
        url = mediaRetrieverController.currentUrl,
        initialPosition = mediaRetrieverController.streamPlaybackPosition
    )

    private suspend inline fun onFetchVideoClicked(url: String) {
        sendPlaybackPosition(0)
        storeCurrentUrl(url)
        storeAndPlayNewStream(url)
    }

    private fun launchMonitoringTasks() {
        scope.launch { startNotificationObserving() }
        scope.launch { startAudioEffectsMonitoring() }
    }

    override fun onDestroy() {
        super.onDestroy()
        isConnectedState.update { false }
        playbackTask?.cancel()
        detachNotification()
        releaseMedia()
        unregisterReceivers()
    }

    // --------------------------- Notification Setup ---------------------------

    private inline val Actions.playbackIntent: PendingIntent
        get() = PendingIntent.getBroadcast(
            this@StreamService,
            requestCode,
            Intent(playbackAction),
            PendingIntent.FLAG_IMMUTABLE
        )

    private val playerNotificationManager by lazy {
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
            ): Bitmap? {
                scope.launch(Dispatchers.IO) {
                    callback.onBitmap(getVideoCoverAsync().await())
                }

                return null
            }
        }

    private val customActionsReceiver: PlayerNotificationManager.CustomActionReceiver =
        object : PlayerNotificationManager.CustomActionReceiver {
            override fun createCustomActions(
                context: Context,
                instanceId: Int
            ) = mutableMapOf(
                ACTION_REPEAT to repeatActionCompat,
                ACTION_UNREPEAT to unrepeatActionCompat,
                ACTION_DISMISS to dismissNotificationActionCompat
            )

            override fun getCustomActions(player: Player) = newCustomActions

            override fun onCustomAction(player: Player, action: String, intent: Intent) =
                sendBroadcast(commandsToActions[action]!!.playbackAction)
        }

    private inline val newCustomActions
        get() = mutableListOf(
            when {
                mediaRetrieverController.isRepeating -> ACTION_REPEAT
                else -> ACTION_UNREPEAT
            },
            ACTION_DISMISS
        )

    private val commandsToActions = mapOf(
        ACTION_PAUSE to Actions.Pause,
        ACTION_RESUME to Actions.Resume,
        ACTION_10_SECS_BACK to Actions.TenSecsBack,
        ACTION_10_SECS_FORWARD to Actions.TenSecsForward,
        ACTION_REPEAT to Actions.Repeat,
        ACTION_UNREPEAT to Actions.Unrepeat,
        ACTION_DISMISS to Actions.Dismiss
    )

    // --------------------------- Notification Handle ---------------------------

    /**
     * Runs loop that observers all notification related states
     * and updates notification when something has changed
     */

    private suspend inline fun startNotificationObserving() =
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            combine(
                mediaRetrieverController.isPlayingState,
                mediaRetrieverController.isRepeatingState
            ) { isPlaying, isRepeating ->
                isPlaying to isRepeating
            }.collectLatest {
                scope.launch { updateNotification() }
            }
        }

    private suspend inline fun updateNotification() {
        updateMediaSession()
        playerNotificationManager.invalidate()
    }

    private fun detachNotification() {
        Log.d(TAG, "Notification is remove")

        @Suppress("DEPRECATION")
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> stopForeground(STOP_FOREGROUND_REMOVE)
            else -> stopForeground(true)
        }
    }

    // --------------------------- Notification Actions ---------------------------

    private inline val repeatActionCompat
        get() = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(
                this,
                R.drawable.repeat
            ),
            getString(R.string.change_repeat),
            Actions.Repeat.playbackIntent
        ).build()

    private inline val unrepeatActionCompat
        get() = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(
                this,
                R.drawable.no_repeat
            ),
            getString(R.string.change_repeat),
            Actions.Unrepeat.playbackIntent
        ).build()

    private inline val dismissNotificationActionCompat
        get() = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(this, R.drawable.dismiss),
            getString(R.string.cancel),
            Actions.Dismiss.playbackIntent
        ).build()

    // ----------------------- Storage Handler Utils -----------------------

    private suspend inline fun storePlaybackPosition() =
        mediaRetrieverController.storeStreamPlaybackPosition(currentPlaybackPosition)

    private suspend inline fun storeIsRepeating(isRepeating: Boolean) =
        mediaRetrieverController.storeIsRepeating(isRepeating)

    private suspend inline fun storeCurrentUrl(url: String) =
        mediaRetrieverController.storeCurrentUrl(url)

    private suspend inline fun storeMetadata(videoMeta: VideoMeta?) =
        mediaRetrieverController.storeCurrentMetadata(videoMeta?.let(::VideoMetadata))

    // --------------------------- Error Handle ---------------------------

    @Suppress("DEPRECATION")
    private fun ErrorNotification(message: String) = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
            Notification.Builder(applicationContext, STREAM_CHANNEL_ID)

        else -> Notification.Builder(applicationContext)
    }
        .setSmallIcon(R.drawable.save_icon)
        .setContentIntent(
            PendingIntent.getActivity(
                applicationContext,
                0,
                Intent(applicationContext, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        .setContentTitle(message)
        .setAutoCancel(true)
        .setShowWhen(false)
        .build()

    private fun sendErrorBroadcast(error: Throwable) {
        val errorMessage = error.message ?: getString(R.string.unknown_error)
        startForeground(NOTIFICATION_ID, ErrorNotification(errorMessage))
        isStoppedWithError = true

        sendBroadcast(
            Intent(MainActivity.Broadcast_STREAMING_ERROR).putExtra(
                MainActivity.STREAMING_ERROR_ARG,
                errorMessage
            )
        )
    }
}