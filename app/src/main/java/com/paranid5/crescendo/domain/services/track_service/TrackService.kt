package com.paranid5.crescendo.domain.services.track_service

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
import android.widget.Toast
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
import com.paranid5.crescendo.TRACK_SERVICE_CONNECTION
import com.paranid5.crescendo.data.tracks.DefaultTrack
import com.paranid5.crescendo.data.utils.extensions.toAndroidMetadata
import com.paranid5.crescendo.domain.LifecycleNotificationManager
import com.paranid5.crescendo.domain.ReceiverManager
import com.paranid5.crescendo.domain.services.SuspendService
import com.paranid5.crescendo.domain.services.service_controllers.MediaRetrieverController
import com.paranid5.crescendo.domain.services.service_controllers.MediaSessionController
import com.paranid5.crescendo.domain.services.service_controllers.PlaybackController
import com.paranid5.crescendo.domain.utils.extensions.registerReceiverCompat
import com.paranid5.crescendo.domain.utils.extensions.sendBroadcast
import com.paranid5.crescendo.domain.utils.extensions.toMediaItemList
import com.paranid5.crescendo.presentation.main.MainActivity
import com.paranid5.crescendo.presentation.main.playing.Broadcast_CUR_POSITION_CHANGED
import com.paranid5.crescendo.presentation.main.playing.CUR_POSITION_ARG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

@OptIn(UnstableApi::class)
class TrackService : SuspendService(),
    ReceiverManager,
    LifecycleNotificationManager,
    KoinComponent {
    @Suppress("IncorrectFormatting")
    companion object {
        private val TAG = TrackService::class.simpleName!!

        internal const val NOTIFICATION_ID = 102
        private const val AUDIO_CHANNEL_ID = "stream_channel"
        private const val PLAYBACK_UPDATE_COOLDOWN = 500L

        private const val SERVICE_LOCATION = "com.paranid5.crescendo.domain.services.track_service"

        const val Broadcast_PAUSE = "$SERVICE_LOCATION.PAUSE"
        const val Broadcast_RESUME = "$SERVICE_LOCATION.RESUME"
        const val Broadcast_SWITCH_PLAYLIST = "$SERVICE_LOCATION.SWITCH_PLAYLIST"

        const val Broadcast_ADD_TO_PLAYLIST = "$SERVICE_LOCATION.ADD_TO_PLAYLIST"
        const val Broadcast_REMOVE_FROM_PLAYLIST = "$SERVICE_LOCATION.REMOVE_FROM_PLAYLIST"
        const val Broadcast_PLAYLIST_DRAGGED = "$SERVICE_LOCATION.PLAYLIST_DRAGGED"

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

        const val TRACK_ARG = "track"
        const val PLAYLIST_ARG = "playlist"
        const val TRACK_INDEX_ARG = "track_index"
        const val POSITION_ARG = "position"

        @Suppress("DEPRECATION")
        private inline val Intent.trackArgOrNull
            get() = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                    getParcelableExtra(TRACK_ARG, DefaultTrack::class.java)

                else -> getParcelableExtra(TRACK_ARG)
            }

        @Suppress("DEPRECATION")
        private inline val Intent.playlistArgOrNull
            @Suppress("UNCHECKED_CAST")
            get() = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                    getParcelableArrayExtra(PLAYLIST_ARG, DefaultTrack::class.java)

                else -> getParcelableArrayExtra(PLAYLIST_ARG) as Array<DefaultTrack>
            }?.toList()

        private inline val Intent.trackArg
            get() = trackArgOrNull!!

        private inline val Intent.playlistArg
            get() = playlistArgOrNull!!

        private inline val Intent.trackIndexArg
            get() = getIntExtra(TRACK_INDEX_ARG, 0)
    }

    private val isConnectedState by inject<MutableStateFlow<Boolean>>(
        named(TRACK_SERVICE_CONNECTION)
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
                scope.launch { resumePlayback() }
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
                scope.launch { switchToNextTrack() }
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                scope.launch { switchToPrevTrack() }
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
                    playbackController.isPlaying -> PlaybackStateCompat.STATE_PLAYING
                    else -> PlaybackStateCompat.STATE_PAUSED
                },
                exoPlaybackPosition,
                mediaRetrieverController.speed,
                SystemClock.elapsedRealtime()
            )
            .build()

    private fun PlaybackStateCompat.Builder.setCustomActions(): PlaybackStateCompat.Builder {
        val repeatAction = when {
            mediaRetrieverController.isRepeating -> PlaybackStateCompat.CustomAction.Builder(
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

        val dismissAction = PlaybackStateCompat.CustomAction.Builder(
            ACTION_DISMISS,
            resources.getString(R.string.cancel),
            R.drawable.dismiss
        ).build()

        return this
            .addCustomAction(repeatAction)
            .addCustomAction(dismissAction)
    }

    private fun initMediaSession() {
        mediaSessionController.initMediaSession(
            mediaSessionCallback = newMediaSessionCallback,
            playbackState = newPlaybackState
        )

        playerNotificationManager.setPlayer(playbackController.player)
    }

    private suspend fun updateMediaSession(track: DefaultTrack) = mediaSession.run {
        setPlaybackState(newPlaybackState)
        setMetadata(track.toAndroidMetadata(getTrackCoverAsync(track.path).await()))
    }

    private suspend fun getTrackCoverAsync(path: String?) =
        mediaRetrieverController.getTrackCoverBitmapAsync(path)

    // ----------------------- Player Management -----------------------

    @Volatile
    private var isStoppedWithError = false

    private val playbackController by lazy {
        PlaybackController(
            context = this,
            playerStateChangedListener = playerStateChangedListener,
            mediaRetrieverController = mediaRetrieverController
        )
    }

    private val playerStateChangedListener: Player.Listener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)

            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO)
                updateTrackIndexAsync()
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)

            if (reason == Player.DISCONTINUITY_REASON_AUTO_TRANSITION)
                storePositionAndUpdateNotificationAsync()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)

            when (playbackState) {
                Player.STATE_IDLE -> scope.launch {
                    restartPlayer(initialPosition = exoPlaybackPosition)
                }

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

            Toast.makeText(
                applicationContext,
                "${getString(R.string.error)}: ${error.message ?: getString(R.string.unknown_error)}",
                Toast.LENGTH_LONG
            ).show()
        }

        private fun updateTrackIndexAsync() = scope.launch {
            mediaRetrieverController.storeCurrentTrackIndex(playbackController.currentMediaItemIndex)
        }

        private fun storePositionAndUpdateNotificationAsync() = scope.launch {
            sendAndStorePlaybackPosition()
            updateNotification()
        }
    }

    // ----------------------- Playback Management  -----------------------

    private fun resetPlaylistForPlayer(playlist: List<DefaultTrack>) =
        playbackController.player.run {
            clearMediaItems()
            addMediaItems(playlist.toMediaItemList())
        }

    private fun resetAudioSessionIdIfNotPlaying() {
        if (!playbackController.isPlaying) playbackController.resetAudioSessionId()
    }

    // ----------------------- Playback Handle -----------------------

    private inline val exoPlaybackPosition
        get() = playbackController.currentPosition

    private inline val exoTrackIndex
        get() = playbackController.currentMediaItemIndex

    private inline val currentTrackOrNull
        get() = mediaRetrieverController.currentTrack

    private inline val currentTrack
        get() = currentTrackOrNull!!

    private inline val currentPlaylistOrNull
        get() = mediaRetrieverController.currentPlaylist

    private inline val currentPlaylist
        get() = currentPlaylistOrNull!!

    private inline val savedTrackIndex
        get() = mediaRetrieverController.currentTrackIndex

    private inline val savedPlaybackPosition
        get() = mediaRetrieverController.playbackPosition

    private fun sendPlaybackPosition(curPosition: Long = exoPlaybackPosition) = sendBroadcast(
        Intent(Broadcast_CUR_POSITION_CHANGED).putExtra(CUR_POSITION_ARG, curPosition)
    )

    private suspend fun sendAndStorePlaybackPosition() {
        sendPlaybackPosition()
        storePlaybackPosition()
    }

    private fun pausePlayback() {
        scope.launch { sendAndStorePlaybackPosition() }
        playbackController.pause()
    }

    private suspend fun resumePlayback() = playPlaylist(
        playlist = currentPlaylist,
        curTrackInd = exoTrackIndex,
        initialPosition = exoPlaybackPosition
    )

    @OptIn(UnstableApi::class)
    private suspend fun playPlaylist(
        playlist: List<DefaultTrack>,
        curTrackInd: Int,
        initialPosition: Long = 0
    ) {
        Log.d(TAG, "Playing track with index $curTrackInd: ${playlist[curTrackInd]}")

        resetAudioSessionIdIfNotPlaying()
        updateMediaSession(track = playlist[curTrackInd])
        playerNotificationManager.invalidate()

        playbackController.player.run {
            seekTo(curTrackInd, initialPosition)
            prepare()
            playWhenReady = true
        }
    }

    private suspend fun restartPlayer(initialPosition: Long = 0) =
        currentPlaylistOrNull?.let {
            resetPlaylistForPlayer(playlist = it)

            playPlaylist(
                playlist = it,
                curTrackInd = savedTrackIndex,
                initialPosition = initialPosition
            )
        }

    private fun seekTo(position: Long) {
        resetAudioSessionIdIfNotPlaying()
        playbackController.player.seekTo(position)
    }

    private suspend fun storeAndSwitchToTrackAt(index: Int) {
        storeCurrentTrackIndex(index)
        resetAudioSessionIdIfNotPlaying()
        playbackController.seekToTrackAtDefaultPosition(index)
    }

    private suspend fun switchToPrevTrack() = storeAndSwitchToTrackAt(
        index = when {
            playbackController.hasPreviousMediaItem -> playbackController.previousMediaItemIndex
            else -> currentPlaylistOrNull?.size?.let { it - 1 } ?: 0
        }
    )

    private suspend fun switchToNextTrack() = storeAndSwitchToTrackAt(
        index = when {
            playbackController.hasNextMediaItem -> playbackController.nextMediaItemIndex
            else -> 0
        }
    )

    private fun releaseMedia() {
        playerNotificationManager.setPlayer(null)
        playbackController.releaseAudioEffects()
        playbackController.releasePlayer()
        mediaSessionController.releaseMediaSession()
    }

    // --------------------------- Playback Monitoring ---------------------------

    private lateinit var playbackPosMonitorTask: Job

    private fun startPlaybackPositionMonitoring() {
        playbackPosMonitorTask = scope.launch {
            while (true) {
                sendAndStorePlaybackPosition()
                delay(PLAYBACK_UPDATE_COOLDOWN)
            }
        }
    }

    private fun stopPlaybackPositionMonitoring() = playbackPosMonitorTask.cancel()

    private suspend fun startAudioEffectsMonitoring() =
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

            scope.launch {
                when {
                    isStoppedWithError -> {
                        restartPlayer(initialPosition = exoPlaybackPosition)
                        isStoppedWithError = false
                    }

                    else -> resumePlayback()
                }
            }
        }
    }

    private val switchPlaylistReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.d(TAG, "switch playlist")
            val playlist = intent.playlistArg
            val trackInd = intent.trackIndexArg
            scope.launch { onTrackClicked(playlist, trackInd) }
        }
    }

    private val addToPlaylistReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val track = intent.trackArg
            Log.d(TAG, "Add track $track to playlist")
            playbackController.addMediaItem(MediaItem.fromUri(track.path))
        }
    }

    private val removeFromPlaylistReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val index = intent.trackIndexArg
            Log.d(TAG, "Remove $index track from playlist")
            playbackController.removeMediaItem(index)
        }
    }

    private val playlistDraggedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.d(TAG, "on playlist dragged")

            val newPlaylist = intent.playlistArg
            val newTrackInd = intent.trackIndexArg

            playbackController.setMediaItems(
                newPlaylist.toMediaItemList(),
                newTrackInd,
                playbackController.currentPosition
            )
        }
    }

    private val switchToPrevTrackReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "prev track")
            scope.launch { switchToPrevTrack() }
        }
    }

    private val switchToNextTrackReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "next track")
            scope.launch { switchToNextTrack() }
        }
    }

    private val seekToReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val position = intent.getLongExtra(POSITION_ARG, 0)
            Log.d(TAG, "seek to $position")
            seekTo(position)
        }
    }

    private val repeatChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            scope.launch {
                val newRepeatMode = !mediaRetrieverController.isRepeating
                storeIsRepeating(newRepeatMode)
                playbackController.repeatMode = PlaybackController.getRepeatMode(newRepeatMode)
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
    override fun registerReceivers() {
        registerReceiverCompat(pauseReceiver, Broadcast_PAUSE)
        registerReceiverCompat(resumeReceiver, Broadcast_RESUME)
        registerReceiverCompat(switchPlaylistReceiver, Broadcast_SWITCH_PLAYLIST)
        registerReceiverCompat(addToPlaylistReceiver, Broadcast_ADD_TO_PLAYLIST)
        registerReceiverCompat(removeFromPlaylistReceiver, Broadcast_REMOVE_FROM_PLAYLIST)
        registerReceiverCompat(playlistDraggedReceiver, Broadcast_PLAYLIST_DRAGGED)
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
        unregisterReceiver(playlistDraggedReceiver)
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

        val playlist = intent?.playlistArgOrNull
        val trackInd = intent?.trackIndexArg

        scope.launch {
            when (playlist) {
                // Resume received
                null -> onResumeClicked()

                // New playlist received
                else -> onTrackClicked(playlist, trackInd!!)
            }

            launchMonitoringTasks()
        }

        return START_STICKY
    }

    private suspend fun onResumeClicked() {
        resetPlaylistForPlayer(currentPlaylist)

        playPlaylist(
            playlist = currentPlaylist,
            curTrackInd = savedTrackIndex,
            initialPosition = savedPlaybackPosition
        )
    }

    private suspend fun onTrackClicked(playlist: List<DefaultTrack>, trackInd: Int) {
        val newCurTrackPath = playlist[trackInd].path
        val prevCurTrackPath = currentTrackOrNull?.path

        Log.d(TAG, "Track with index $trackInd is clicked")
        Log.d(TAG, "New track: $newCurTrackPath; Old track: $prevCurTrackPath")

        val currentTrackIndex = exoTrackIndex
        val currentPosition = savedPlaybackPosition

        storeCurrentPlaylist(playlist)
        storeCurrentTrackIndex(trackInd)

        when {
            newCurTrackPath == prevCurTrackPath && playbackController.isPlaying ->
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
        pausePlayback()
        resetPlaylistForPlayer(playlist)
        playbackController.player.seekTo(currentTrackIndex, currentPosition)
    }

    private suspend inline fun resumeOnTrackClicked(
        playlist: List<DefaultTrack>,
        trackIndex: Int,
        currentPosition: Long
    ) {
        Log.d(TAG, "Resume after click")
        resetPlaylistForPlayer(playlist)
        playPlaylist(playlist, trackIndex, currentPosition)
    }

    private suspend inline fun newPlaylistOnTrackClicked(
        playlist: List<DefaultTrack>,
        trackInd: Int
    ) {
        Log.d(TAG, "New playlist on click")
        pausePlayback()
        resetPlaylistForPlayer(playlist)
        playPlaylist(playlist, trackInd)
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

    // --------------------------- Notification Setup ---------------------------

    private inline val Actions.playbackIntent: PendingIntent
        get() = PendingIntent.getBroadcast(
            this@TrackService,
            requestCode,
            Intent(playbackAction),
            PendingIntent.FLAG_IMMUTABLE
        )

    private val playerNotificationManager by lazy {
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
            if (!dismissedByUser) pausePlayback()
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
                currentTrackOrNull?.title ?: getString(R.string.unknown_track)

            override fun createCurrentContentIntent(player: Player) = PendingIntent.getActivity(
                applicationContext,
                0,
                Intent(applicationContext, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            override fun getCurrentContentText(player: Player) =
                currentTrackOrNull?.artist ?: getString(R.string.unknown_artist)

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? {
                scope.launch(Dispatchers.IO) {
                    callback.onBitmap(getTrackCoverAsync(path = currentTrackOrNull?.path).await())
                }

                return null
            }
        }

    private val customActionsReceiver =
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

    private val newCustomActions
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
        ACTION_PREV_TRACK to Actions.PrevTrack,
        ACTION_NEXT_TRACK to Actions.NextTrack,
        ACTION_REPEAT to Actions.Repeat,
        ACTION_UNREPEAT to Actions.Unrepeat,
        ACTION_DISMISS to Actions.Dismiss
    )

    // --------------------------- Notification Handle ---------------------------

    /**
     * Runs loop that observers all notification related states
     * and updates notification when something has changed
     */

    override suspend fun startNotificationObserving() =
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            combine(
                mediaRetrieverController.isPlayingState,
                mediaRetrieverController.isRepeatingState,
                mediaRetrieverController.currentTrackIndexState,
            ) { isPlaying, isRepeating, curTrackInd ->
                Triple(isPlaying, isRepeating, curTrackInd)
            }.collectLatest {
                scope.launch { updateNotification() }
            }
        }

    private suspend fun updateNotification() {
        Log.d(TAG, "Update Notification; track: $currentTrackOrNull")
        updateMediaSession(currentTrack)
        playerNotificationManager.invalidate()
    }

    override fun detachNotification() {
        Log.d(TAG, "Notification is removed")

        @Suppress("DEPRECATION")
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> stopForeground(STOP_FOREGROUND_REMOVE)
            else -> stopForeground(true)
        }
    }

    // --------------------------- Notification Actions ---------------------------

    private val repeatActionCompat
        get() = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(
                this,
                R.drawable.repeat
            ),
            resources.getString(R.string.change_repeat),
            Actions.Repeat.playbackIntent
        ).build()

    private val unrepeatActionCompat
        get() = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(
                this,
                R.drawable.no_repeat
            ),
            resources.getString(R.string.change_repeat),
            Actions.Unrepeat.playbackIntent
        ).build()

    private val dismissNotificationActionCompat
        get() = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(this, R.drawable.dismiss),
            resources.getString(R.string.cancel),
            Actions.Dismiss.playbackIntent
        ).build()

    // ----------------------- Storage Handler Utils -----------------------

    private suspend inline fun storePlaybackPosition() =
        mediaRetrieverController.storeTracksPlaybackPosition(exoPlaybackPosition)

    private suspend inline fun storeIsRepeating(isRepeating: Boolean) =
        mediaRetrieverController.storeIsRepeating(isRepeating)

    private suspend inline fun storeCurrentPlaylist(playlist: List<DefaultTrack>) =
        mediaRetrieverController.storeCurrentPlaylist(playlist)

    private suspend inline fun storeCurrentTrackIndex(index: Int) =
        mediaRetrieverController.storeCurrentTrackIndex(index)
}