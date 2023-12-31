package com.paranid5.crescendo.services.stream_service.playback

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import arrow.core.Tuple4
import com.paranid5.crescendo.services.stream_service.StreamService2
import com.paranid5.crescendo.services.stream_service.extractor.extractMediaFilesAndStartPlaying
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

suspend fun PlaybackEventLoop(service: StreamService2): MutableSharedFlow<PlaybackEvent> {
    val playbackEventFlow = MutableSharedFlow<PlaybackEvent>()

    service.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        combine(
            service.playerProvider.currentUrlFlow,
            service.playerProvider.streamPlaybackPositionFlow,
            service.playerProvider.currentMetadataFlow,
            playbackEventFlow
        ) { url, position, metadata, event ->
            Tuple4(url, position, metadata?.durationMillis ?: 0L, event)
        }.collectLatest { (ytUrl, position, duration, event) ->
            when (event) {
                PlaybackEvent.StartSameStream ->
                    service.onPlayStream(
                        ytUrl = ytUrl,
                        initialPosition = position
                    )

                is PlaybackEvent.StartNewStream ->
                    service.onPlayStream(
                        ytUrl = event.ytUrl,
                        initialPosition = event.initialPosition
                    )

                PlaybackEvent.Pause -> service.onPause()

                PlaybackEvent.Resume -> service.onResume(
                    ytUrl = ytUrl,
                    initialPosition = position
                )

                is PlaybackEvent.SeekTo ->
                    service.onSeekTo(event.position)

                PlaybackEvent.SeekTenSecsBack ->
                    service.onSeekTenSecsBack()

                PlaybackEvent.SeekTenSecsForward ->
                    service.onSeekTenSecsForward(videoDurationMillis = duration)
            }
        }
    }

    return playbackEventFlow
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