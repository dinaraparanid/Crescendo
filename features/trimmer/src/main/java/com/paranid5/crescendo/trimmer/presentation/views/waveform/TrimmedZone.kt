package com.paranid5.crescendo.trimmer.presentation.views.waveform

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_HEIGHT_OFFSET
import com.paranid5.crescendo.trimmer.presentation.DEFAULT_GRAPHICS_LAYER_ALPHA
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerPositionBroadcast
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectEndOffsetAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectIsPlayingAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectStartOffsetAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectTrackDurationInMillisAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun TrimmedZone(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val startOffset by viewModel.collectStartOffsetAsState()
    val endOffset by viewModel.collectEndOffsetAsState()

    val progressColor = colors.primary
    val progressBrush = remember(colors.primary) {
        SolidColor(progressColor.copy(alpha = 0.25F))
    }

    Canvas(modifier.trimmedZoneModifier()) {
        drawRect(
            brush = progressBrush,
            topLeft = Offset(startOffset * size.width, 0F),
            size = Size(
                width = (endOffset - startOffset) * size.width,
                height = size.height - CONTROLLER_HEIGHT_OFFSET
            )
        )
    }
}

@Composable
private fun Modifier.trimmedZoneModifier() = this
    .graphicsLayer(alpha = DEFAULT_GRAPHICS_LAYER_ALPHA)
    .playbackPointerInput()

@Composable
private fun Modifier.playbackPointerInput(
    viewModel: TrimmerViewModel = koinViewModel(),
): Modifier {
    val isPlaying by viewModel.collectIsPlayingAsState()
    return if (isPlaying) playbackModifier() else this
}

@Composable
private fun Modifier.playbackModifier(
    viewModel: TrimmerViewModel = koinViewModel(),
): Modifier {
    val positionBroadcast = LocalTrimmerPositionBroadcast.current
    val durationMillis by viewModel.collectTrackDurationInMillisAsState()
    val coroutineScope = rememberCoroutineScope()

    return this.pointerInput(Unit) {
        detectTapGestures {
            coroutineScope.sendPlaybackPosition(
                positionBroadcast = positionBroadcast,
                offset = it.x,
                size = size.width,
                durationMillis = durationMillis
            )
        }
    }
}

private fun CoroutineScope.sendPlaybackPosition(
    positionBroadcast: MutableSharedFlow<Long>,
    offset: Float,
    size: Int,
    durationMillis: Long
) = launch {
    positionBroadcast.emit(
        playbackPosition(
            offset = offset,
            size = size,
            durationMillis = durationMillis
        )
    )
}

private fun playbackPosition(offset: Float, size: Int, durationMillis: Long) =
    (offset / size * durationMillis).toLong()