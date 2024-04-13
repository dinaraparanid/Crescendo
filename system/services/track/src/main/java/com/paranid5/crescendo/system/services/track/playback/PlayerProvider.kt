package com.paranid5.crescendo.system.services.track.playback

import androidx.annotation.MainThread
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.playback.TracksPlaybackPositionPublisherImpl
import com.paranid5.crescendo.data.sources.playback.TracksPlaybackPositionSubscriberImpl
import com.paranid5.crescendo.data.sources.tracks.*
import com.paranid5.crescendo.domain.repositories.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.sources.playback.TracksPlaybackPositionPublisher
import com.paranid5.crescendo.domain.sources.playback.TracksPlaybackPositionSubscriber
import com.paranid5.crescendo.domain.sources.tracks.*
import com.paranid5.crescendo.system.services.track.TrackService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.component.KoinComponent

@Suppress("IncorrectFormatting")
internal class PlayerProvider(
    service: TrackService,
    storageRepository: StorageRepository,
    currentPlaylistRepository: CurrentPlaylistRepository
) : KoinComponent,
    PlayerController by PlayerControllerImpl(service, storageRepository),
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