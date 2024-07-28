package com.paranid5.crescendo.system.services.track.playback

import androidx.annotation.MainThread
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.playback.TracksPlaybackPositionPublisherImpl
import com.paranid5.crescendo.data.sources.playback.TracksPlaybackPositionSubscriberImpl
import com.paranid5.crescendo.data.sources.tracks.CurrentPlaylistPublisherImpl
import com.paranid5.crescendo.data.sources.tracks.CurrentPlaylistSubscriberImpl
import com.paranid5.crescendo.data.sources.tracks.CurrentTrackIndexPublisherImpl
import com.paranid5.crescendo.data.sources.tracks.CurrentTrackIndexSubscriberImpl
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsRepository
import com.paranid5.crescendo.domain.repositories.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.sources.playback.TracksPlaybackPositionPublisher
import com.paranid5.crescendo.domain.sources.playback.TracksPlaybackPositionSubscriber
import com.paranid5.crescendo.domain.sources.tracks.CurrentPlaylistPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentPlaylistSubscriber
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackIndexPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackIndexSubscriber
import com.paranid5.crescendo.system.services.track.TrackService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class PlayerProvider(
    service: TrackService,
    storageRepository: StorageRepository,
    currentPlaylistRepository: CurrentPlaylistRepository,
    audioEffectsRepository: AudioEffectsRepository,
) : PlayerController by PlayerControllerImpl(service, storageRepository, audioEffectsRepository),
    CurrentPlaylistSubscriber by CurrentPlaylistSubscriberImpl(currentPlaylistRepository),
    CurrentPlaylistPublisher by CurrentPlaylistPublisherImpl(currentPlaylistRepository),
    CurrentTrackIndexSubscriber by CurrentTrackIndexSubscriberImpl(storageRepository),
    CurrentTrackIndexPublisher by CurrentTrackIndexPublisherImpl(storageRepository),
    TracksPlaybackPositionSubscriber by TracksPlaybackPositionSubscriberImpl(storageRepository),
    TracksPlaybackPositionPublisher by TracksPlaybackPositionPublisherImpl(storageRepository) {
    private val _playbackEventFlow by lazy {
        MutableSharedFlow<PlaybackEvent>()
    }

    val playbackEventFlow by lazy {
        _playbackEventFlow.asSharedFlow()
    }

    @Volatile
    var isStoppedWithError = false

    val areAudioEffectsEnabledFlow by lazy {
        audioEffectsRepository.areAudioEffectsEnabledFlow
    }

    val speedFlow by lazy {
        audioEffectsRepository.speedFlow
    }

    val pitchFlow by lazy {
        audioEffectsRepository.pitchFlow
    }

    val equalizerBandsFlow by lazy {
        audioEffectsRepository.equalizerBandsFlow
    }

    val equalizerPresetFlow by lazy {
        audioEffectsRepository.equalizerPresetFlow
    }

    val equalizerParamFlow by lazy {
        audioEffectsRepository.equalizerParamFlow
    }

    val bassStrengthFlow by lazy {
        audioEffectsRepository.bassStrengthFlow
    }

    val reverbPresetFlow by lazy {
        audioEffectsRepository.reverbPresetFlow
    }

    inline val currentPosition
        get() = currentPositionState.value

    suspend fun storePlaybackPosition() =
        setTracksPlaybackPosition(currentPosition)

    var playbackParameters
        @MainThread get() = player.playbackParameters
        @MainThread set(value) {
            player.playbackParameters = value
        }

    suspend fun playPlaylist() =
        _playbackEventFlow.emit(PlaybackEvent.StartNewPlaylist())

    suspend fun startResuming() =
        _playbackEventFlow.emit(PlaybackEvent.StartSamePlaylist())

    suspend fun resume() =
        _playbackEventFlow.emit(PlaybackEvent.Resume())

    suspend fun pause() =
        _playbackEventFlow.emit(PlaybackEvent.Pause())

    suspend fun seekTo(position: Long) =
        _playbackEventFlow.emit(PlaybackEvent.SeekTo(position))

    suspend fun seekToNextTrack() =
        _playbackEventFlow.emit(PlaybackEvent.SeekToNextTrack())

    suspend fun seekToPrevTrack() =
        _playbackEventFlow.emit(PlaybackEvent.SeekToPrevTrack())

    suspend fun addTrackToPlaylist(track: Track) =
        _playbackEventFlow.emit(PlaybackEvent.AddTrackToPlaylist(track))

    suspend fun removeTrackFromPlaylist(index: Int) =
        _playbackEventFlow.emit(PlaybackEvent.RemoveTrackFromPlaylist(index))

    suspend fun replacePlaylist() =
        _playbackEventFlow.emit(PlaybackEvent.ReplacePlaylist())
}

internal suspend inline fun PlayerProvider.restartPlayer() = startResuming()

internal suspend inline fun PlayerProvider.updateCurrentTrackIndex() =
    setCurrentTrackIndex(currentMediaItemIndex)