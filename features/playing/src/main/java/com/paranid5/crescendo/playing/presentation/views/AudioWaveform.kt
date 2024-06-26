package com.paranid5.crescendo.playing.presentation.views

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.palette.graphics.Palette
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer
import com.paranid5.crescendo.core.impl.di.AUDIO_SESSION_ID
import com.paranid5.crescendo.core.impl.di.IS_PLAYING
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
internal fun AudioWaveform(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    palette: Palette? = null,
    audioSessionIdState: MutableStateFlow<Int> = koinInject(named(AUDIO_SESSION_ID)),
    isPlayingState: MutableStateFlow<Boolean> = koinInject(named(IS_PLAYING))
) {
    val color = palette.getBrightDominantOrPrimary()
    val audioSessionId by audioSessionIdState.collectLatestAsState()
    val isPlaying by isPlayingState.collectLatestAsState()
    var visualizer: WaveVisualizer? = null

    DisposableEffect(enabled, color, audioSessionId, isPlaying) {
        when {
            enabled && isPlaying -> visualizer?.recompose(color, audioSessionId)
            !isPlaying -> visualizer?.stop()
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
        update = {
            visualizer = it

            if (enabled)
                it.recompose(color, audioSessionId)
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