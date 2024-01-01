package com.paranid5.crescendo.services.stream_service.playback

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import arrow.core.Tuple4
import com.paranid5.crescendo.services.stream_service.StreamService2
import com.paranid5.crescendo.services.stream_service.extractor.extractMediaFilesAndStartPlaying
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

suspend fun PlaybackEventLoop(service: StreamService2): MutableSharedFlow<PlaybackEvent> {
    val playbackEventFlow = MutableSharedFlow<PlaybackEvent>()

    service.serviceScope.launch {
        service.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            playbackEventFlow
                .combine(ArgsFlow(service)) { event, (url, position, metadata) ->
                    Tuple4(url, position, metadata, event)
                }
                .distinctUntilChanged { (_, _, _, e1), (_, _, _, e2) -> e1 == e2 }
                .collectLatest { (ytUrl, position, duration, event) ->
                    service.onEvent(ytUrl, position, duration, event)
                }
        }
    }

    return playbackEventFlow
}

private fun ArgsFlow(service: StreamService2) =
    combine(
        service.playerProvider.currentUrlFlow,
        service.playerProvider.streamPlaybackPositionFlow,
        service.playerProvider.currentMetadataFlow,
    ) { url, position, metadata ->
        Triple(url, position, metadata?.durationMillis ?: 0L)
    }.distinctUntilChanged()

private suspend inline fun StreamService2.onEvent(
    ytUrl: String,
    position: Long,
    duration: Long,
    event: PlaybackEvent
) = when (event) {
    is PlaybackEvent.StartSameStream -> onPlayStream(
        ytUrl = ytUrl,
        initialPosition = position
    )

    is PlaybackEvent.StartNewStream -> onPlayStream(
        ytUrl = event.ytUrl,
        initialPosition = event.initialPosition
    )

    is PlaybackEvent.Pause -> onPause()

    is PlaybackEvent.Resume -> onResume(
        ytUrl = ytUrl,
        initialPosition = position
    )

    is PlaybackEvent.SeekTo -> onSeekTo(event.position)

    is PlaybackEvent.SeekTenSecsBack -> onSeekTenSecsBack()

    is PlaybackEvent.SeekTenSecsForward -> onSeekTenSecsForward(
        videoDurationMillis = duration
    )
}

private suspend inline fun StreamService2.onPlayStream(ytUrl: String, initialPosition: Long) {
    playerProvider.resetAudioSessionIdIfNotPlaying()
    extractMediaFilesAndStartPlaying(ytUrl, initialPosition)
}

private suspend fun StreamService2.onPause() {
    playerProvider.setStreamPlaybackPosition(playerProvider.currentPosition)
    playerProvider.playerController.pause()
}

private suspend inline fun StreamService2.onResume(ytUrl: String, initialPosition: Long) =
    when {
        playerProvider.isStoppedWithError -> {
            onPlayStream(ytUrl, initialPosition)
            playerProvider.isStoppedWithError = false
        }

        else -> playerProvider.playerController.resume()
    }

private fun StreamService2.onSeekTo(position: Long) {
    playerProvider.resetAudioSessionIdIfNotPlaying()
    playerProvider.playerController.seekTo(position)
}

private fun StreamService2.onSeekTenSecsBack() {
    playerProvider.resetAudioSessionIdIfNotPlaying()
    playerProvider.playerController.seekTenSecsBack()
}

private fun StreamService2.onSeekTenSecsForward(videoDurationMillis: Long) {
    playerProvider.resetAudioSessionIdIfNotPlaying()
    playerProvider.playerController.seekTenSecsForward(videoDurationMillis)
}