package com.paranid5.crescendo.presentation.playing

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import com.paranid5.crescendo.AUDIO_SESSION_ID
import com.paranid5.crescendo.R
import com.paranid5.crescendo.presentation.ui.extensions.getLightMutedOrPrimary
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

private const val TAG = "AudioWaveform"

@SuppressLint("LogConditional")
@Composable
fun AudioWaveform(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    palette: Palette? = null,
    audioSessionIdState: MutableStateFlow<Int> = koinInject(named(AUDIO_SESSION_ID))
) {
    val color = palette.getLightMutedOrPrimary()
    val audioSessionId by audioSessionIdState.collectAsState()
    var visualizer: WaveVisualizer? = null

    enabled.let {
        Log.d(TAG, "enabled: $enabled")

        AndroidView(
            modifier = modifier,
            factory = { context ->
                when {
                    enabled -> context
                        .findWaveVisualizer()
                        .also { visualizer = it }
                        .apply { recompose(color, audioSessionId) }

                    else -> View(context)
                }
            },
            update = {
                if (it is WaveVisualizer)
                    it.recompose(color, audioSessionId)
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            visualizer?.release()
            visualizer = null
        }
    }
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
}

@SuppressLint("LogConditional")
private fun WaveVisualizer.updateAudioSessionId(audioSessionId: Int) {
    try {
        Log.d(TAG, "Audio Session Id: $audioSessionId")
        setAudioSessionId(audioSessionId)
    } catch (ignored: Exception) {
        // playback is not yet started
    }
}