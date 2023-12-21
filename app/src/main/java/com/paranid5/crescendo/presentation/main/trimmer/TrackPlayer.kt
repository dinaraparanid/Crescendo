package com.paranid5.crescendo.presentation.main.trimmer

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import com.paranid5.crescendo.domain.tracks.Track
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

private const val PLAYBACK_UPDATE_COOLDOWN = 500L
private const val TRANSITION_DURATION = 10_000

inline fun TrackPlayer(track: Track, crossinline onCompletion: (MediaPlayer) -> Unit) =
    MediaPlayer().apply {
        setDataSource(track.path)

        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .build()
        )

        setOnCompletionListener { onCompletion(this) }

        prepare()
    }

fun MediaPlayer.seekTenSecsBack(startPosition: Int) {
    seekTo(maxOf(currentPosition - TRANSITION_DURATION, startPosition))
}

fun MediaPlayer.seekTenSecsForward(totalDuration: Int) {
    seekTo(minOf(currentPosition + TRANSITION_DURATION, totalDuration))
}

fun MediaPlayer.stopAndReleaseCatching() = runCatching {
    stop()
    release()
}

suspend fun PlaybackPositionMonitoringTask(
    player: MediaPlayer,
    trimmerViewModel: TrimmerViewModel
) = coroutineScope {
    while (player.isPlaying) {
        trimmerViewModel.setPlaybackPosition(player.currentPosition.toLong())
        delay(PLAYBACK_UPDATE_COOLDOWN)
    }
}