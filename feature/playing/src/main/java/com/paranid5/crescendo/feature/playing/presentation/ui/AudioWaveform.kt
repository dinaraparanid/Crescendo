package com.paranid5.crescendo.feature.playing.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.feature.playing.view_model.PlayingState

@Composable
internal fun AudioWaveform(
    color: Color,
    state: PlayingState,
    modifier: Modifier = Modifier,
) {
    val isWaveformEnabled = state.isScreenAudioStatusActual
    val audioSessionId = state.audioSessionId
    val isPlaying = state.isPlaying

    var visualizer: WaveVisualizer? = null

    DisposableEffect(isWaveformEnabled, color, audioSessionId, isPlaying) {
        when {
            isWaveformEnabled && isPlaying ->
                visualizer?.recompose(color, audioSessionId)

            isPlaying.not() -> visualizer?.stop()
        }

        onDispose {
            visualizer?.release()
            visualizer = null
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            context
                .findWaveVisualizer()
                .also { visualizer = it }
        },
        update = { wave ->
            visualizer = wave

            if (isWaveformEnabled)
                wave.recompose(color, state.audioSessionId)
        }
    )
}

@SuppressLint("InflateParams")
private fun Context.findWaveVisualizer() =
    LayoutInflater
        .from(this)
        .inflate(R.layout.audio_waveform, null, false)
            as WaveVisualizer

private fun WaveVisualizer.recompose(color: Color, audioSessionId: Int) {
    setColor(color.toArgb())
    updateAudioSessionId(audioSessionId)
    invalidate()
}

private fun WaveVisualizer.updateAudioSessionId(audioSessionId: Int) {
    try {
        setAudioSessionId(audioSessionId)
    } catch (ignored: Exception) {
        // playback is not yet started
    }
}

private fun WaveVisualizer.stop() =
    updateAudioSessionId(0)
