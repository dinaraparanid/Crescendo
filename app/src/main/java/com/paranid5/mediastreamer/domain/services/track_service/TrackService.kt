package com.paranid5.mediastreamer.domain.services.track_service

import android.app.Notification
import android.app.PendingIntent
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
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MOVIE
import androidx.media3.common.C.USAGE_MEDIA
import androidx.media3.common.C.WAKE_MODE_NETWORK
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.NotificationUtil
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import com.paranid5.mediastreamer.AUDIO_SESSION_ID
import com.paranid5.mediastreamer.EQUALIZER_DATA
import com.paranid5.mediastreamer.IS_PLAYING_STATE
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.TRACK_SERVICE_CONNECTION
import com.paranid5.mediastreamer.data.eq.EqualizerData
import com.paranid5.mediastreamer.data.tracks.DefaultTrack
import com.paranid5.mediastreamer.data.utils.extensions.toAndroidMetadata
import com.paranid5.mediastreamer.domain.LifecycleNotificationManager
import com.paranid5.mediastreamer.domain.ReceiverManager
import com.paranid5.mediastreamer.domain.services.ServiceAction
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.domain.services.SuspendService
import com.paranid5.mediastreamer.domain.utils.extensions.bandLevels
import com.paranid5.mediastreamer.domain.utils.extensions.registerReceiverCompat
import com.paranid5.mediastreamer.domain.utils.extensions.sendBroadcast
import com.paranid5.mediastreamer.domain.utils.extensions.setParameter
import com.paranid5.mediastreamer.presentation.main_activity.MainActivity
import com.paranid5.mediastreamer.presentation.playing.Broadcast_CUR_POSITION_CHANGED
import com.paranid5.mediastreamer.presentation.playing.CUR_POSITION_ARG
import com.paranid5.mediastreamer.presentation.ui.utils.GlideUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

@OptIn(androidx.media3.common.util.UnstableApi::class)
class TrackService : SuspendService(), ReceiverManager, LifecycleNotificationManager, KoinComponent {
    companion object {
        private const val NOTIFICATION_ID = 102
        private const val AUDIO_CHANNEL_ID = "stream_channel"
        private const val PLAYBACK_UPDATE_COOLDOWN = 500L

        private const val SERVICE_LOCATION = "com.paranid5.mediastreamer.domain.services.track_service"

        const val Broadcast_PAUSE = "$SERVICE_LOCATION.PAUSE"
        const val Broadcast_RESUME = "$SERVICE_LOCATION.RESUME"
        const val Broadcast_SWITCH_PLAYLIST = "$SERVICE_LOCATION.SWITCH_PLAYLIST"

        const val Broadcast_ADD_TO_PLAYLIST = "$SERVICE_LOCATION.ADD_TO_PLAYLIST"
        const val Broadcast_REMOVE_FROM_PLAYLIST = "$SERVICE_LOCATION.REMOVE_FROM_PLAYLIST"

        const val Broadcast_PREV_TRACK = "$SERVICE_LOCATION.PREV_TRACK"
        const val Broadcast_NEXT_TRACK = "$SERVICE_LOCATION.NEXT_TRACK"
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
        private const val ACTION_PREV_TRACK = "prev_track"
        private const val ACTION_NEXT_TRACK = "next_track"
        private const val ACTION_REPEAT = "repeat"
        private const val ACTION_UNREPEAT = "unrepeat"
        private const val ACTION_DISMISS = "dismiss"

        private val commandsToActions = mapOf(
            ACTION_PAUSE to Actions.Pause,
            ACTION_RESUME to Actions.Resume,
            ACTION_PREV_TRACK to Actions.PrevTrack,
            ACTION_NEXT_TRACK to Actions.NextTrack,
            ACTION_REPEAT to Actions.Repeat,
            ACTION_UNREPEAT to Actions.Unrepeat,
            ACTION_DISMISS to Actions.Dismiss
        )

        const val TRACK_ARG = "track"
        const val PLAYLIST_ARG = "playlist"
        const val TRACK_INDEX_ARG = "track_index"
        const val POSITION_ARG = "position"

        private val TAG = TrackService::class.simpleName!!

        internal inline val Intent.mTrackArgOrNull
            get() = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                    getParcelableExtra(TRACK_ARG, DefaultTrack::class.java)

                else -> getParcelableExtra(TRACK_ARG)
            }

        internal inline val Intent.mPlaylistArgOrNull
            @Suppress("UNCHECKED_CAST")
            get() = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                    getParcelableArrayExtra(PLAYLIST_ARG, DefaultTrack::class.java)

                else -> getParcelableArrayExtra(PLAYLIST_ARG) as Array<DefaultTrack>
            }?.toList()

        internal inline val Intent.mTrackArg
            get() = mTrackArgOrNull!!

        internal inline val Intent.mPlaylistArg
            get() = mPlaylistArgOrNull!!

        internal inline val Intent.mTrackIndexArg
            get() = getIntExtra(TRACK_INDEX_ARG, 0)

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

        data object PrevTrack : Actions(
            requestCode = NOTIFICATION_ID + 3,
            playbackAction = Broadcast_PREV_TRACK
        )

        data object NextTrack : Actions(
            requestCode = NOTIFICATION_ID + 4,
            playbackAction = Broadcast_NEXT_TRACK
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
            this@TrackService,
            requestCode,
            Intent(playbackAction),
            PendingIntent.FLAG_IMMUTABLE
        )

    private val storageHandler by inject<StorageHandler>()
    private val glideUtils by inject<GlideUtils> { parametersOf(this) }

    private val currentTrackIndexState = storageHandler.currentTrackIndexState
    private val currentPlaylistState = storageHandler.currentPlaylistState
    private val currentTrackState = storageHandler.currentTrackState

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
        named(TRACK_SERVICE_CONNECTION)
    )

    private val startIdState = MutableStateFlow(0)

    private lateinit var playbackPosMonitorTask: Job

    @Volatile
    private var isStoppedWithError = false

    private inline val exoTrackIndex
        get() = mPlayer.currentMediaItemIndex

    internal inline val mExoPlaybackPosition
        get() = mPlayer.currentPosition

    private inline val savedPlaybackPosition
        get() = playbackPositionState.value

    internal inline val mSavedTrackIndex
        get() = currentTrackIndexState.value

    internal inline val mCurrentPlaylistOrNull
        get() = currentPlaylistState.value

    private inline val currentPlaylist
        get() = mCurrentPlaylistOrNull!!

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
        PlayerNotificationManager.Builder(this, NOTIFICATION_ID, AUDIO_CHANNEL_ID)
            .setChannelNameResourceId(R.string.app_name)
            .setChannelDescriptionResourceId(R.string.app_name)
            .setChannelImportance(NotificationUtil.IMPORTANCE_HIGH)
            .setNotificationListener(notificationListener)
            .setMediaDescriptionAdapter(mediaDescriptionProvider)
            .setCustomActionReceiver(customActionsReceiver)
            .setNextActionIconResourceId(R.drawable.next_track)
            .setPreviousActionIconResourceId(R.drawable.prev_track)
            .setPlayActionIconResourceId(R.drawable.play)
            .setPauseActionIconResourceId(R.drawable.pause)
            .build()
            .apply {
                setUseStopAction(false)
                setUseChronometer(false)
                setUseFastForwardAction(false)
                setUseRewindAction(false)
                setUseFastForwardActionInCompactView(false)
                setUseRewindActionInCompactView(false)

                setPriority(NotificationCompat.PRIORITY_HIGH)
                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                setMediaSessionToken(mediaSession.sessionToken)
            }
    }

    private val notificationListener = object : PlayerNotificationManager.NotificationListener {
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            super.onNotificationCancelled(notificationId, dismissedByUser)
            detachNotification()
            if (!dismissedByUser) mPausePlayback()
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
                currentTrackState.value?.title ?: getString(R.string.unknown_track)

            override fun createCurrentContentIntent(player: Player) = PendingIntent.getActivity(
                applicationContext,
                0,
                Intent(applicationContext, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            override fun getCurrentContentText(player: Player) =
                currentTrackState.value?.artist ?: getString(R.string.unknown_artist)

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ) = runBlocking { mGetTrackCoverAsync(path = currentTrackState.value?.path).await() }
        }

    private val customActionsReceiver =
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
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)

            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) scope.launch {
                mStoreCurrentTrackIndex(mPlayer.currentMediaItemIndex)
            }
        }

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
                    mRestartPlayer(initialPosition = mExoPlaybackPosition)
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

            scope.launch {
                when {
                    isStoppedWithError -> {
                        mRestartPlayer(initialPosition = mExoPlaybackPosition)
                        isStoppedWithError = false
                    }

                    else -> mResumePlayback()
                }
            }
        }
    }

    private val switchPlaylistReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.d(TAG, "switch playlist")
            val playlist = intent.mPlaylistArg
            val trackInd = intent.mTrackIndexArg
            scope.launch { mOnTrackClicked(playlist, trackInd) }
        }
    }

    private val addToPlaylistReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val track = intent.mTrackArg
            Log.d(TAG, "Add track $track to playlist")
            mPlayer.addMediaItem(MediaItem.fromUri(track.path))
        }
    }

    private val removeFromPlaylistReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val index = intent.getIntExtra(TRACK_INDEX_ARG, 0)
            Log.d(TAG, "Remove $index track from playlist")
            mPlayer.removeMediaItem(index)
        }
    }

    private val switchToPrevTrackReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "prev track")
            scope.launch { mSwitchToPrevTrack() }
        }
    }

    private val switchToNextTrackReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "next track")
            scope.launch { mSwitchToNextTrack() }
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
        override fun onReceive(context: Context?, intent: Intent?) =
            bassBoost.setStrength(bassStrengthState.value)
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
        registerReceiverCompat(switchPlaylistReceiver, Broadcast_SWITCH_PLAYLIST)
        registerReceiverCompat(addToPlaylistReceiver, Broadcast_ADD_TO_PLAYLIST)
        registerReceiverCompat(removeFromPlaylistReceiver, Broadcast_REMOVE_FROM_PLAYLIST)
        registerReceiverCompat(switchToPrevTrackReceiver, Broadcast_PREV_TRACK)
        registerReceiverCompat(switchToNextTrackReceiver, Broadcast_NEXT_TRACK)
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
        unregisterReceiver(switchPlaylistReceiver)
        unregisterReceiver(addToPlaylistReceiver)
        unregisterReceiver(removeFromPlaylistReceiver)
        unregisterReceiver(switchToPrevTrackReceiver)
        unregisterReceiver(switchToNextTrackReceiver)
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

        val playlist = intent?.mPlaylistArgOrNull
        val trackInd = intent?.mTrackIndexArg

        scope.launch {
            when (playlist) {
                // Resume received
                null -> onResumeClicked()

                // New playlist received
                else -> mOnTrackClicked(playlist, trackInd!!)
            }

            launchMonitoringTasks()
        }

        return START_STICKY
    }

    private suspend inline fun onResumeClicked() {
        mResetPlaylistForPlayer(currentPlaylist)

        mPlayPlaylist(
            playlist = currentPlaylist,
            curTrackInd = mSavedTrackIndex,
            initialPosition = savedPlaybackPosition
        )
    }

    internal suspend inline fun mOnTrackClicked(playlist: List<DefaultTrack>, trackInd: Int) {
        val newCurTrackPath = playlist[trackInd].path
        val prevCurTrackPath = currentTrackState.value?.path

        Log.d(TAG, "Track with index $trackInd is clicked")
        Log.d(TAG, "New track: $newCurTrackPath; Old track: $prevCurTrackPath")

        val currentTrackIndex = exoTrackIndex
        val currentPosition = savedPlaybackPosition

        mStoreCurrentPlaylist(playlist)
        mStoreCurrentTrackIndex(trackInd)

        when {
            newCurTrackPath == prevCurTrackPath && mPlayer.isPlaying ->
                pauseOnTrackClicked(playlist, currentTrackIndex, currentPosition)

            newCurTrackPath == prevCurTrackPath ->
                resumeOnTrackClicked(playlist, trackInd, currentPosition)

            else -> newPlaylistOnTrackClicked(playlist, trackInd)
        }
    }

    private fun pauseOnTrackClicked(
        playlist: List<DefaultTrack>,
        currentTrackIndex: Int,
        currentPosition: Long
    ) {
        Log.d(TAG, "Pause after click")
        mPausePlayback()
        mResetPlaylistForPlayer(playlist)
        mPlayer.seekTo(currentTrackIndex, currentPosition)
    }

    private suspend inline fun resumeOnTrackClicked(
        playlist: List<DefaultTrack>,
        trackIndex: Int,
        currentPosition: Long
    ) {
        Log.d(TAG, "Resume after click")
        mResetPlaylistForPlayer(playlist)
        mPlayPlaylist(playlist, trackIndex, currentPosition)
    }

    private suspend inline fun newPlaylistOnTrackClicked(
        playlist: List<DefaultTrack>,
        trackInd: Int
    ) {
        Log.d(TAG, "New playlist on click")
        mPausePlayback()
        mResetPlaylistForPlayer(playlist)
        mPlayPlaylist(playlist, trackInd)
    }

    private fun launchMonitoringTasks() {
        scope.launch { startNotificationObserving() }
        scope.launch { startAudioEffectsMonitoring() }
    }

    override fun onDestroy() {
        super.onDestroy()
        isConnectedState.update { false }
        detachNotification()
        releaseMedia()
        unregisterReceivers()
        Log.d(TAG, "TrackService is destroyed")
    }

    // ----------------------- Playback Handle -----------------------

    private fun sendPlaybackPosition(curPosition: Long = mExoPlaybackPosition) = sendBroadcast(
        Intent(Broadcast_CUR_POSITION_CHANGED).putExtra(CUR_POSITION_ARG, curPosition)
    )

    internal suspend inline fun mSendAndStorePlaybackPosition() {
        sendPlaybackPosition()
        storePlaybackPosition()
    }

    /**
     * Updates playback position for the UI,
     * pauses both the player and the audio effects
     */

    internal fun mPausePlayback() {
        scope.launch { mSendAndStorePlaybackPosition() }
        mPlayer.pause()
        mSetAudioEffectsEnabled(areEnabled = false)
    }

    private fun ExoPlayer.initAudioEffects() {
        initEqualizer(audioSessionId)
        initBassBoost(audioSessionId)
        initReverb(audioSessionId)

        if (areAudioEffectsEnabledState.value)
            playbackParameters = PlaybackParameters(speedState.value, pitchState.value)
    }

    /** Resumes the player and the audio effects */

    internal suspend inline fun mResumePlayback() = mPlayPlaylist(
        playlist = currentPlaylist,
        curTrackInd = exoTrackIndex,
        initialPosition = mExoPlaybackPosition
    )

    // ----------------------- Storage Handler Utils -----------------------

    private suspend inline fun storePlaybackPosition() =
        storageHandler.storePlaybackPosition(mExoPlaybackPosition)

    internal suspend inline fun mStoreIsRepeating(isRepeating: Boolean) =
        storageHandler.storeIsRepeating(isRepeating)

    internal suspend inline fun mStoreCurrentPlaylist(playlist: List<DefaultTrack>) =
        storageHandler.storeCurrentPlaylist(playlist)

    internal suspend inline fun mStoreCurrentTrackIndex(index: Int) {
        Log.d(TAG, "Saving track index: $index")
        storageHandler.storeCurrentTrackIndex(index)
    }

    // ----------------------- Playback Management  -----------------------

    internal fun mResetPlaylistForPlayer(playlist: List<DefaultTrack>) = mPlayer.run {
        clearMediaItems()
        playlist.forEach { track -> addMediaItem(MediaItem.fromUri(track.path)) }
    }

    private fun resetAudioSessionId() {
        releaseAudioEffects()
        audioSessionIdState.update { mPlayer.audioSessionId }
        mPlayer.initAudioEffects()
        mSetAudioEffectsEnabled(areEnabled = areAudioEffectsEnabledState.value)
    }

    private fun resetAudioSessionIdIfNotPlaying() {
        if (!mPlayer.isPlaying) resetAudioSessionId()
    }

    /**
     * Resets [playlist] for the player
     * and starts playback from the given track
     * by its [curTrackInd] with the given [initialPosition]
     */

    @OptIn(UnstableApi::class)
    internal suspend inline fun mPlayPlaylist(
        playlist: List<DefaultTrack>,
        curTrackInd: Int,
        initialPosition: Long = 0
    ) {
        Log.d(TAG, "Playing track with index $curTrackInd: ${playlist[curTrackInd]}")

        resetAudioSessionIdIfNotPlaying()
        mUpdateMediaSession(track = playlist[curTrackInd])
        mPlayerNotificationManager.invalidate()

        mPlayer.run {
            seekTo(curTrackInd, initialPosition)
            prepare()
            playWhenReady = true
        }
    }

    /**
     * Resets playlist for the player with [mCurrentPlaylistOrNull]
     * and starts the playback from the track with [mSavedTrackIndex]
     * with the given [initialPosition]
     */

    internal suspend inline fun mRestartPlayer(initialPosition: Long = 0) =
        mCurrentPlaylistOrNull?.let {
            mResetPlaylistForPlayer(playlist = it)

            mPlayPlaylist(
                playlist = it,
                curTrackInd = mSavedTrackIndex,
                initialPosition = initialPosition
            )
        }

    internal fun mSeekTo(position: Long) {
        resetAudioSessionIdIfNotPlaying()
        mPlayer.seekTo(position)
    }

    private suspend inline fun storeAndSwitchToTrackAt(index: Int) {
        mStoreCurrentTrackIndex(index)
        resetAudioSessionIdIfNotPlaying()
        mPlayer.seekToDefaultPosition(index)
    }

    /**
     * Stores previous track as the current one and switches to it.
     * If current track is the first one,
     * switches to the last one from the playlist
     */

    internal suspend inline fun mSwitchToPrevTrack() = storeAndSwitchToTrackAt(
        index = when {
            mPlayer.hasPreviousMediaItem() -> mPlayer.previousMediaItemIndex
            else -> mCurrentPlaylistOrNull?.size?.let { it - 1 } ?: 0
        }
    )

    /**
     * Stores next track as the current one and switches to it.
     * If current track is the last one,
     * switches to the next one from the playlist
     */

    internal suspend inline fun mSwitchToNextTrack() = storeAndSwitchToTrackAt(
        index = when {
            mPlayer.hasNextMediaItem() -> mPlayer.nextMediaItemIndex
            else -> 0
        }
    )

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

    internal fun mSetAudioEffectsEnabled(areEnabled: Boolean) {
        mPlayer.playbackParameters = when {
            areEnabled -> PlaybackParameters(speedState.value, pitchState.value)
            else -> PlaybackParameters(1F, 1F)
        }

        // For some reason, it requires multiple tries to enable...
        repeat(3) {
            try {
                equalizer.enabled = areEnabled
                bassBoost.enabled = areEnabled
                reverb.enabled = areEnabled
            } catch (ignored: IllegalStateException) {
                // not initialized
            }
        }
    }

    private fun releaseAudioEffects() {
        mSetAudioEffectsEnabled(areEnabled = false)
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
                scope.launch { mResumePlayback() }
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
                scope.launch { mSwitchToNextTrack() }
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                scope.launch { mSwitchToPrevTrack() }
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
                mExoPlaybackPosition,
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

    internal suspend inline fun mUpdateMediaSession(track: DefaultTrack) = mediaSession.run {
        setPlaybackState(newPlaybackState)
        setMetadata(track.toAndroidMetadata(mGetTrackCoverAsync(track.path).await()))
    }

    internal suspend inline fun mGetTrackCoverAsync(path: String?) =
        glideUtils.getTrackCoverBitmapAsync(path)

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
            combine(
                mIsPlayingState,
                mIsRepeatingState,
                currentTrackIndexState,
            ) { isPlaying, isRepeating, curTrackInd ->
                Triple(isPlaying, isRepeating, curTrackInd)
            }.collectLatest {
                scope.launch { mUpdateNotification() }
            }
        }

    internal suspend inline fun mUpdateNotification() {
        val currentTrack = currentTrackState.value ?: return
        Log.d(TAG, "Update Notification; track: $currentTrack")
        mUpdateMediaSession(currentTrack)
        mPlayerNotificationManager.invalidate()
    }

    override fun detachNotification() {
        Log.d(TAG, "Notification is removed")
        isNotificationShown = false

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> stopForeground(STOP_FOREGROUND_REMOVE)
            else -> stopForeground(true)
        }
    }
}