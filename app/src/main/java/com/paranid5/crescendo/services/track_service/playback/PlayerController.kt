package com.paranid5.crescendo.services.track_service.playback

import androidx.annotation.OptIn
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.paranid5.crescendo.AUDIO_SESSION_ID
import com.paranid5.crescendo.IS_PLAYING
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.states.playback.RepeatingStatePublisher
import com.paranid5.crescendo.data.states.playback.RepeatingStatePublisherImpl
import com.paranid5.crescendo.data.states.playback.RepeatingStateSubscriber
import com.paranid5.crescendo.data.states.playback.RepeatingStateSubscriberImpl
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.domain.utils.extensions.toMediaItem
import com.paranid5.crescendo.domain.utils.extensions.toMediaItemList
import com.paranid5.crescendo.services.core.playback.AudioEffectsController
import com.paranid5.crescendo.services.core.playback.AudioEffectsControllerImpl
import com.paranid5.crescendo.services.track_service.TrackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

interface PlayerController :
    AudioEffectsController,
    RepeatingStateSubscriber,
    RepeatingStatePublisher {
    val player: Player

    var isPlaying: Boolean
    val isPlayingState: StateFlow<Boolean>

    val currentPositionState: StateFlow<Long>
    fun updateCurrentPosition()

    suspend fun setAndStoreRepeating(isRepeating: Boolean)

    fun playPlaylistViaPlayer(
        playlist: List<Track>,
        currentTrackIndex: Int,
        initialPosition: Long
    )

    fun pausePlayer()

    fun resumePlayer()

    fun seekToViaPlayer(position: Long)

    fun seekToPreviousTrackViaPlayer(playlistSize: Int): Int

    fun seekToNextTrackViaPlayer(): Int

    fun resetAudioSessionIdIfNotPlaying()

    fun addTrackToPlaylistViaPlayer(track: Track)

    fun removeTrackViaPlayer(index: Int): Int

    fun replacePlaylistViaPlayer(newPlaylist: List<Track>, newCurrentTrackIndex: Int)

    fun releasePlayerWithEffects()
}

internal class PlayerControllerImpl(service: TrackService, storageHandler: StorageHandler) :
    PlayerController, KoinComponent,
    AudioEffectsController by AudioEffectsControllerImpl(storageHandler),
    RepeatingStateSubscriber by RepeatingStateSubscriberImpl(storageHandler),
    RepeatingStatePublisher by RepeatingStatePublisherImpl(storageHandler) {
    private val _isPlayingState by inject<MutableStateFlow<Boolean>>(named(IS_PLAYING))

    private val audioSessionIdState by inject<MutableStateFlow<Int>>(named(AUDIO_SESSION_ID))

    @OptIn(UnstableApi::class)
    override val player by lazy {
        ExoPlayer.Builder(service)
            .setAudioAttributes(newAudioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setPauseAtEndOfMediaItems(false)
            .build()
            .apply {
                addListener(PlayerStateChangedListener(service))
                audioSessionIdState.update { audioSessionId }
                initAudioEffects(audioSessionId)

                service.serviceScope.launch {
                    startRepeatMonitoring(service.lifecycle)
                }
            }
    }

    override var isPlaying
        get() = _isPlayingState.value
        set(value) = _isPlayingState.update { value }

    override val isPlayingState by lazy {
        _isPlayingState.asStateFlow()
    }

    private val _currentPositionState by lazy {
        MutableStateFlow(0L)
    }

    override val currentPositionState by lazy {
        _currentPositionState.asStateFlow()
    }

    inline val currentPosition
        get() = currentPositionState.value

    override fun updateCurrentPosition() =
        _currentPositionState.update { player.currentPosition }

    override suspend fun setAndStoreRepeating(isRepeating: Boolean) {
        repeatMode = repeatMode(isRepeating)
        setRepeating(isRepeating)
    }

    override fun playPlaylistViaPlayer(
        playlist: List<Track>,
        currentTrackIndex: Int,
        initialPosition: Long
    ) {
        resetPlaylistForPlayer(playlist)
        player.playWhenReady = true
        player.prepare()
        player.seekTo(currentTrackIndex, initialPosition)
    }

    override fun pausePlayer() = player.pause()

    override fun resumePlayer() {
        resetAudioSessionId()
        play()
    }

    private fun resetPlaylistForPlayer(playlist: List<Track>) {
        player.clearMediaItems()
        player.addMediaItems(playlist.toMediaItemList())
    }

    private fun play() {
        player.playWhenReady = true
    }

    override fun resetAudioSessionIdIfNotPlaying() {
        if (!isPlaying) resetAudioSessionId()
    }

    @OptIn(UnstableApi::class)
    private fun resetAudioSessionId() =
        audioSessionIdState.update { player.audioSessionId }

    override fun seekToViaPlayer(position: Long) {
        resetAudioSessionId()
        player.seekTo(position)
    }

    override fun seekToPreviousTrackViaPlayer(playlistSize: Int): Int {
        seekToTrackAtDefaultPosition(previousMediaItemIndex(playlistSize))
        return currentMediaItemIndex
    }

    override fun seekToNextTrackViaPlayer(): Int {
        seekToTrackAtDefaultPosition(nextMediaItemIndex)
        return currentMediaItemIndex
    }

    private fun seekToTrackAtDefaultPosition(index: Int) =
        player.seekToDefaultPosition(index)

    private inline val hasPreviousMediaItem
        get() = player.hasPreviousMediaItem()

    private inline val hasNextMediaItem
        get() = player.hasNextMediaItem()

    private fun previousMediaItemIndex(playlistSize: Int) = when {
        hasPreviousMediaItem -> previousMediaItemIndexUnsafe
        else -> maxOf(playlistSize - 1, 0)
    }

    private inline val nextMediaItemIndex
        get() = when {
            hasNextMediaItem -> nextMediaItemIndexUnsafe
            else -> 0
        }

    private inline val previousMediaItemIndexUnsafe
        get() = player.previousMediaItemIndex

    private inline val nextMediaItemIndexUnsafe
        get() = player.nextMediaItemIndex

    private inline val currentMediaItemIndex
        get() = player.currentMediaItemIndex

    override fun addTrackToPlaylistViaPlayer(track: Track) =
        player.addMediaItem(track.toMediaItem())

    override fun removeTrackViaPlayer(index: Int): Int {
        player.removeMediaItem(index)
        return currentMediaItemIndex
    }

    override fun replacePlaylistViaPlayer(newPlaylist: List<Track>, newCurrentTrackIndex: Int) =
        player.setMediaItems(
            newPlaylist.toMediaItemList(),
            newCurrentTrackIndex,
            currentPosition
        )

    private suspend inline fun startRepeatMonitoring(lifecycle: Lifecycle): Unit =
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            isRepeatingFlow
                .distinctUntilChanged()
                .collectLatest { player.repeatMode = repeatMode(it) }
        }

    override fun releasePlayerWithEffects() {
        releaseAudioEffects()
        player.stop()
        player.release()
        audioSessionIdState.update { 0 }
    }
}

inline var PlayerController.repeatMode
    get() = player.repeatMode
    set(value) {
        player.repeatMode = value
    }

var PlayerController.isRepeating
    get() = player.repeatMode == Player.REPEAT_MODE_ONE
    set(value) {
        player.repeatMode = repeatMode(value)
    }

@OptIn(UnstableApi::class)
private inline val newAudioAttributes
    get() = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .setUsage(C.USAGE_MEDIA)
        .build()

private fun repeatMode(isRepeating: Boolean) = when {
    isRepeating -> Player.REPEAT_MODE_ONE
    else -> Player.REPEAT_MODE_ALL
}