package com.paranid5.crescendo.system.services.track.playback

import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsRepository
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.playback.PlaybackRepository.Companion.UNDEFINED_AUDIO_SESSION_ID
import com.paranid5.crescendo.domain.playback.RepeatingPublisher
import com.paranid5.crescendo.domain.playback.RepeatingSubscriber
import com.paranid5.crescendo.system.services.track.TrackService
import com.paranid5.crescendo.utils.extensions.sideEffect
import com.paranid5.crescendo.utils.extensions.toMediaItem
import com.paranid5.crescendo.utils.extensions.toMediaItemList
import com.paranid5.system.services.common.playback.AudioEffectsController
import com.paranid5.system.services.common.playback.AudioEffectsControllerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update

internal class PlayerControllerImpl(
    service: TrackService,
    audioEffectsRepository: AudioEffectsRepository,
    private val playbackRepository: PlaybackRepository,
) : PlayerController,
    AudioEffectsController by AudioEffectsControllerImpl(audioEffectsRepository),
    RepeatingSubscriber by playbackRepository,
    RepeatingPublisher by playbackRepository {

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
                playbackRepository.updateAudioSessionId(audioSessionId)
                initAudioEffects(audioSessionId)
                startRepeatMonitoring(service.serviceScope)
            }
    }

    override var isPlaying
        get() = playbackRepository.isPlayingState.value
        set(value) = playbackRepository.updatePlaying(isPlaying = value)

    override val isPlayingState by lazy {
        playbackRepository.isPlayingState
    }

    private val _currentPositionState by lazy {
        MutableStateFlow(0L)
    }

    override val currentPositionState by lazy {
        _currentPositionState.asStateFlow()
    }

    private inline val currentPosition
        get() = currentPositionState.value

    override fun fetchPositionFromPlayer() =
        updateCurrentPosition(position = player.currentPosition)

    override fun resetPosition() = updateCurrentPosition(position = 0)

    private fun updateCurrentPosition(position: Long) =
        _currentPositionState.update { position }

    override suspend fun updateAndStoreRepeating(isRepeating: Boolean) {
        repeatMode = repeatMode(isRepeating)
        updateRepeating(isRepeating)
    }

    override fun playPlaylistViaPlayer(
        playlist: List<Track>,
        currentTrackIndex: Int,
        initialPosition: Long,
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
        if (isPlaying.not()) resetAudioSessionId()
    }

    @OptIn(UnstableApi::class)
    private fun resetAudioSessionId() =
        playbackRepository.updateAudioSessionId(player.audioSessionId)

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

    override fun replacePlaylistViaPlayer(
        newPlaylist: List<Track>,
        newCurrentTrackIndex: Int,
    ) = player.setMediaItems(
        newPlaylist.toMediaItemList(),
        newCurrentTrackIndex,
        currentPosition
    )

    private fun startRepeatMonitoring(scope: CoroutineScope): Unit =
        scope.sideEffect {
            isRepeatingFlow
                .distinctUntilChanged()
                .collectLatest { player.repeatMode = repeatMode(it) }
        }

    override fun releasePlayerWithEffects() {
        releaseAudioEffects()
        player.stop()
        player.release()
        playbackRepository.updateAudioSessionId(UNDEFINED_AUDIO_SESSION_ID)
    }
}

@OptIn(UnstableApi::class)
private inline val newAudioAttributes
    get() = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .setUsage(C.USAGE_MEDIA)
        .build()