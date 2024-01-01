package com.paranid5.crescendo.services.track_service.playback

import androidx.annotation.MainThread
import com.paranid5.crescendo.data.StorageHandler
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
import com.paranid5.crescendo.domain.utils.AsyncCondVar
import com.paranid5.crescendo.services.track_service.TrackService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.component.KoinComponent
import java.util.concurrent.atomic.AtomicInteger

private const val PLAYBACK_EVENT_LOOP_INIT_STEPS = 2

@Suppress("IncorrectFormatting")
class PlayerProvider(service: TrackService, storageHandler: StorageHandler) : KoinComponent,
    PlayerController by PlayerControllerImpl(service, storageHandler),
    CurrentPlaylistStateSubscriber by CurrentPlaylistStateSubscriberImpl(storageHandler),
    CurrentPlaylistStatePublisher by CurrentPlaylistStatePublisherImpl(storageHandler),
    CurrentTrackIndexStateSubscriber by CurrentTrackIndexStateSubscriberImpl(storageHandler),
    CurrentTrackIndexStatePublisher by CurrentTrackIndexStatePublisherImpl(storageHandler),
    TracksPlaybackPositionStateSubscriber by TracksPlaybackPositionStateSubscriberImpl(storageHandler),
    TracksPlaybackPositionStatePublisher by TracksPlaybackPositionStatePublisherImpl(storageHandler) {
    private lateinit var playbackEventFlow: MutableSharedFlow<PlaybackEvent>

    private val eventFlowInitSteps = AtomicInteger()

    private val eventFlowInitCondVar = AsyncCondVar()

    internal suspend inline fun incrementPlaybackEventLoopInitSteps() {
        if (eventFlowInitSteps.incrementAndGet() == PLAYBACK_EVENT_LOOP_INIT_STEPS)
            eventFlowInitCondVar.notify()
    }

    private suspend inline fun waitEventFlowInit() {
        while (eventFlowInitSteps.get() != PLAYBACK_EVENT_LOOP_INIT_STEPS)
            eventFlowInitCondVar.wait()
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

    suspend fun startPlaybackEventLoop(service: TrackService) {
        playbackEventFlow = PlaybackEventLoop(service)
        incrementPlaybackEventLoopInitSteps()
    }

    suspend fun playPlaylist(
        playlist: List<Track>,
        trackIndex: Int,
        initialPosition: Long = 0
    ) {
        waitEventFlowInit()
        playbackEventFlow.emit(
            PlaybackEvent.StartNewPlaylist(
                playlist,
                trackIndex,
                initialPosition
            )
        )
    }

    suspend fun startResuming() {
        waitEventFlowInit()
        delay(500L)
        playbackEventFlow.emit(PlaybackEvent.StartSamePlaylist())
    }

    suspend fun resume() {
        waitEventFlowInit()
        playbackEventFlow.emit(PlaybackEvent.Resume())
    }

    suspend fun pause() {
        waitEventFlowInit()
        playbackEventFlow.emit(PlaybackEvent.Pause())
    }

    suspend fun seekTo(position: Long) {
        waitEventFlowInit()
        playbackEventFlow.emit(PlaybackEvent.SeekTo(position))
    }

    suspend fun seekToNextTrack() {
        waitEventFlowInit()
        playbackEventFlow.emit(PlaybackEvent.SeekToNextTrack())
    }

    suspend fun seekToPrevTrack() {
        waitEventFlowInit()
        playbackEventFlow.emit(PlaybackEvent.SeekToPrevTrack())
    }

    suspend fun addTrackToPlaylist(track: Track) {
        waitEventFlowInit()
        playbackEventFlow.emit(PlaybackEvent.AddTrackToPlaylist(track))
    }

    suspend fun removeTrackFromPlaylist(index: Int) {
        waitEventFlowInit()
        playbackEventFlow.emit(PlaybackEvent.RemoveTrackFromPlaylist(index))
    }

    suspend fun replacePlaylist(newPlaylist: List<Track>, newCurrentTrackIndex: Int) {
        waitEventFlowInit()
        playbackEventFlow.emit(
            PlaybackEvent.ReplacePlaylist(
                newPlaylist,
                newCurrentTrackIndex
            )
        )
    }

    suspend fun restartPlayer() = startResuming()
}