package com.paranid5.mediastreamer.presentation.playing

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.palette.graphics.Palette
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer
import com.paranid5.mediastreamer.AUDIO_SESSION_ID
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.ui.extensions.getLightMutedOrPrimary
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun AudioWaveform(
    modifier: Modifier = Modifier,
    palette: Palette? = null,
    audioSessionIdState: MutableStateFlow<Int> = koinInject(named(AUDIO_SESSION_ID))
) {
    val color = palette.getLightMutedOrPrimary()
    val audioSessionId by audioSessionIdState.collectAsState()
    var visualizer: WaveVisualizer? = null

    AndroidView(
        modifier = modifier,
        factory = { context ->
            context
                .findWaveVisualizer()
                .also { visualizer = it }
                .apply { recompose(color, audioSessionId) }
        },
        update = { it.recompose(color, audioSessionId) }
    )

    DisposableEffect(Unit) {
        onDispose {
            visualizer?.release()
            visualizer = null
        }
    }
}

private fun Context.findWaveVisualizer() =
    LayoutInflater
        .from(this)
        .inflate(R.layout.audio_waveform, null, false)
            as WaveVisualizer

private fun WaveVisualizer.recompose(color: Color, audioSessionId: Int) {
    setColor(color.toArgb())
    updateAudioSessionId(audioSessionId)
}

private fun WaveVisualizer.updateAudioSessionId(audioSessionId: Int) {
    try {
        Log.d("AudioWaveform", "Audio Session Id: $audioSessionId")
        setAudioSessionId(audioSessionId)
    } catch (ignored: Exception) {
        // playback is not yet started
    }
}