package com.paranid5.crescendo.services.track_service.playback

import androidx.annotation.MainThread
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.data.states.playback.TracksPlaybackPositionStatePublisher
import com.paranid5.crescendo.data.states.playback.TracksPlaybackPositionStatePublisherImpl
import com.paranid5.crescendo.data.states.playback.TracksPlaybackPositionStateSubscriber
import com.paranid5.crescendo.data.states.playback.TracksPlaybackPositionStateSubscriberImpl
import com.paranid5.crescendo.data.states.tracks.CurrentPlaylistStatePublisher
import com.paranid5.crescendo.data.states.tracks.CurrentPlaylistStatePublisherImpl
import com.paranid5.crescendo.data.states.tracks.CurrentPlaylistStateSubscriber
import com.paranid5.crescendo.data.states.tracks.CurrentPlaylistStateSubscriberImpl
import com.paranid5.crescendo.data.states.tracks.CurrentTrackIndexStatePublisher
import com.paranid5.crescendo.data.states.tracks.CurrentTrackIndexStatePublisherImpl
import com.paranid5.crescendo.data.states.tracks.CurrentTrackIndexStateSubscriber
import com.paranid5.crescendo.data.states.tracks.CurrentTrackIndexStateSubscriberImpl
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.services.track_service.TrackService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.component.KoinComponent

@Suppress("IncorrectFormatting")
class PlayerProvider(
    service: TrackService,
    storageHandler: StorageHandler,
    currentPlaylistRepository: CurrentPlaylistRepository
) : KoinComponent,
    PlayerController by PlayerControllerImpl(service, storageHandler),
    CurrentPlaylistStateSubscriber by CurrentPlaylistStateSubscriberImpl(currentPlaylistRepository),
    CurrentPlaylistStatePublisher by CurrentPlaylistStatePublisherImpl(currentPlaylistRepository),
    CurrentTrackIndexStateSubscriber by CurrentTrackIndexStateSubscriberImpl(storageHandler),
    CurrentTrackIndexStatePublisher by CurrentTrackIndexStatePublisherImpl(storageHandler),
    TracksPlaybackPositionStateSubscriber by TracksPlaybackPositionStateSubscriberImpl(storageHandler),
    TracksPlaybackPositionStatePublisher by TracksPlaybackPositionStatePublisherImpl(storageHandler) {
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

    suspend fun playPlaylist(
        playlist: List<Track>,
        trackIndex: Int,
        initialPosition: Long = 0
    ) = _playbackEventFlow.emit(
        PlaybackEvent.StartNewPlaylist(
            playlist,
            trackIndex,
            initialPosition
        )
    )

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

    suspend fun replacePlaylist(newPlaylist: List<Track>, newCurrentTrackIndex: Int) =
        _playbackEventFlow.emit(
            PlaybackEvent.ReplacePlaylist(
                newPlaylist,
                newCurrentTrackIndex
            )
        )
}

suspend inline fun PlayerProvider.restartPlayer() = startResuming()

suspend inline fun PlayerProvider.updateCurrentTrackIndex() =
    setCurrentTrackIndex(currentMediaItemIndex)