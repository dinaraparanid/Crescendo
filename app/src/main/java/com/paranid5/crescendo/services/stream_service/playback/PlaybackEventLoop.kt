package com.paranid5.crescendo.services.stream_service.playback

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import arrow.core.Tuple4
import com.paranid5.crescendo.services.stream_service.StreamService
import com.paranid5.crescendo.services.stream_service.extractor.extractMediaFilesAndStartPlaying
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

suspend fun StreamService.startPlaybackEventLoop() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        playerProvider.playbackEventFlow
            .combine(ArgsFlow(this@startPlaybackEventLoop)) { event, (url, position, metadata) ->
                Tuple4(event, url, position, metadata)
            }
            .distinctUntilChanged { (e1, _, _, _), (e2, _, _, _) -> e1 == e2 }
            .collectLatest { (event, ytUrl, position, duration) ->
                onEvent(event, ytUrl, position, duration)
            }
    }

private fun ArgsFlow(service: StreamService) =
    combine(
        service.playerProvider.currentUrlFlow,
        service.playerProvider.streamPlaybackPositionFlow,
        service.playerProvider.currentMetadataFlow,
    ) { url, position, metadata ->
        Triple(url, position, metadata?.durationMillis ?: 0L)
    }.distinctUntilChanged()

private suspend inline fun StreamService.onEvent(
    event: PlaybackEvent,
    ytUrl: String,
    position: Long,
    duration: Long,
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

private suspend inline fun StreamService.onPlayStream(ytUrl: String, initialPosition: Long) {
    playerProvider.resetAudioSessionIdIfNotPlaying()
    extractMediaFilesAndStartPlaying(ytUrl, initialPosition)
}

private suspend fun StreamService.onPause() {
    playerProvider.setStreamPlaybackPosition(playerProvider.currentPosition)
    playerProvider.pausePlayer()
}

private suspend inline fun StreamService.onResume(ytUrl: String, initialPosition: Long) =
    when {
        playerProvider.isStoppedWithError -> {
            onPlayStream(ytUrl, initialPosition)
            playerProvider.isStoppedWithError = false
        }

        else -> playerProvider.resumePlayer()
    }

private fun StreamService.onSeekTo(position: Long) {
    playerProvider.resetAudioSessionIdIfNotPlaying()
    playerProvider.seekToViaPlayer(position)
}

private fun StreamService.onSeekTenSecsBack() {
    playerProvider.resetAudioSessionIdIfNotPlaying()
    playerProvider.seekTenSecsBackViaPlayer()
}

private fun StreamService.onSeekTenSecsForward(videoDurationMillis: Long) {
    playerProvider.resetAudioSessionIdIfNotPlaying()
    playerProvider.seekTenSecsForwardViaPlayer(videoDurationMillis)
}