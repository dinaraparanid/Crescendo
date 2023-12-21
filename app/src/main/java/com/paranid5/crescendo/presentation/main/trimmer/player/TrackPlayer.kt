package com.paranid5.crescendo.presentation.main.trimmer.player

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.effects.playback.pausePlayback
import com.paranid5.crescendo.presentation.main.trimmer.properties.trackOrNullState
import kotlinx.coroutines.launch

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

fun TrackPlayer(viewModel: TrimmerViewModel): MediaPlayer =
    TrackPlayer(
        track = viewModel.trackOrNullState.value!!,
        onCompletion = {
            viewModel.viewModelScope.launch {
                viewModel.pausePlayback()
            }
        }
    )

fun MediaPlayer.seekTenSecsBack(startPosition: Int) =
    seekTo(maxOf(currentPosition - TRANSITION_DURATION, startPosition))

fun MediaPlayer.seekTenSecsForward(totalDuration: Int) =
    seekTo(minOf(currentPosition + TRANSITION_DURATION, totalDuration))

fun MediaPlayer.stopAndReleaseCatching() = runCatching {
    stop()
    release()
}