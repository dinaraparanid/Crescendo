package com.paranid5.crescendo.system.services.track.playback

import androidx.annotation.MainThread
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsEnabledDataSource
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsRepository
import com.paranid5.crescendo.domain.audio_effects.BassStrengthDataSource
import com.paranid5.crescendo.domain.audio_effects.EqualizerBandsDataSource
import com.paranid5.crescendo.domain.audio_effects.EqualizerParamDataSource
import com.paranid5.crescendo.domain.audio_effects.EqualizerPresetDataSource
import com.paranid5.crescendo.domain.audio_effects.PitchDataSource
import com.paranid5.crescendo.domain.audio_effects.ReverbPresetDataSource
import com.paranid5.crescendo.domain.audio_effects.SpeedDataSource
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistPublisher
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistSubscriber
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.playback.TracksPlaybackPositionPublisher
import com.paranid5.crescendo.domain.playback.TracksPlaybackPositionSubscriber
import com.paranid5.crescendo.domain.tracks.CurrentTrackIndexPublisher
import com.paranid5.crescendo.domain.tracks.CurrentTrackIndexSubscriber
import com.paranid5.crescendo.domain.tracks.CurrentTrackSubscriber
import com.paranid5.crescendo.domain.tracks.TracksRepository
import com.paranid5.crescendo.system.services.track.TrackService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class PlayerProvider(
    service: TrackService,
    currentPlaylistRepository: CurrentPlaylistRepository,
    audioEffectsRepository: AudioEffectsRepository,
    playbackRepository: PlaybackRepository,
    tracksRepository: TracksRepository,
) : PlayerController by PlayerControllerImpl(
    service = service,
    audioEffectsRepository = audioEffectsRepository,
    playbackRepository = playbackRepository,
),
    AudioEffectsEnabledDataSource by audioEffectsRepository,
    SpeedDataSource by audioEffectsRepository,
    PitchDataSource by audioEffectsRepository,
    EqualizerBandsDataSource by audioEffectsRepository,
    EqualizerPresetDataSource by audioEffectsRepository,
    EqualizerParamDataSource by audioEffectsRepository,
    BassStrengthDataSource by audioEffectsRepository,
    ReverbPresetDataSource by audioEffectsRepository,
    CurrentPlaylistSubscriber by currentPlaylistRepository,
    CurrentPlaylistPublisher by currentPlaylistRepository,
    CurrentTrackIndexSubscriber by tracksRepository,
    CurrentTrackIndexPublisher by tracksRepository,
    CurrentTrackSubscriber by tracksRepository,
    TracksPlaybackPositionSubscriber by playbackRepository,
    TracksPlaybackPositionPublisher by playbackRepository {
    private val _playbackEventFlow by lazy {
        MutableSharedFlow<PlaybackEvent>()
    }

    val playbackEventFlow by lazy {
        _playbackEventFlow.asSharedFlow()
    }

    @Volatile
    var isStoppedWithError = false

    inline val currentPosition
        get() = currentPositionState.value

    suspend fun storePlaybackPosition() =
        updateTracksPlaybackPosition(currentPosition)

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
    updateCurrentTrackIndex(currentMediaItemIndex)
