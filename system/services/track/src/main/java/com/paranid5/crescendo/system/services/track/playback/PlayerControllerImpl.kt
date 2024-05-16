package com.paranid5.crescendo.system.services.track.playback

import androidx.annotation.OptIn
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.impl.di.AUDIO_SESSION_ID
import com.paranid5.crescendo.core.impl.di.IS_PLAYING
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.playback.RepeatingPublisherImpl
import com.paranid5.crescendo.data.sources.playback.RepeatingSubscriberImpl
import com.paranid5.crescendo.domain.sources.playback.RepeatingPublisher
import com.paranid5.crescendo.domain.sources.playback.RepeatingSubscriber
import com.paranid5.crescendo.system.services.track.TrackService
import com.paranid5.crescendo.utils.extensions.toMediaItem
import com.paranid5.crescendo.utils.extensions.toMediaItemList
import com.paranid5.system.services.common.playback.AudioEffectsController
import com.paranid5.system.services.common.playback.AudioEffectsControllerImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

internal class PlayerControllerImpl(service: TrackService, storageRepository: StorageRepository) :
    PlayerController, KoinComponent,
    AudioEffectsController by AudioEffectsControllerImpl(storageRepository),
    RepeatingSubscriber by RepeatingSubscriberImpl(storageRepository),
    RepeatingPublisher by RepeatingPublisherImpl(storageRepository) {
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

    override suspend fun updateAndStoreRepeating(isRepeating: Boolean) {
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

    override val currentMediaItemIndex
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

@OptIn(UnstableApi::class)
private inline val newAudioAttributes
    get() = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .setUsage(C.USAGE_MEDIA)
        .build()