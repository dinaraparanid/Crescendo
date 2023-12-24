package com.paranid5.crescendo.presentation.main.trimmer.views.waveform

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.paranid5.crescendo.presentation.composition_locals.LocalTrimmerPositionBroadcast
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_HEIGHT_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.DEFAULT_GRAPHICS_LAYER_ALPHA
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.endOffsetFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.isPlayingState
import com.paranid5.crescendo.presentation.main.trimmer.properties.startOffsetFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.trackDurationInMillisFlow
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@Composable
fun TrimmedZone(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val progressBrush = SolidColor(colors.primary.copy(alpha = 0.25F))

    val startOffset by viewModel.startOffsetFlow.collectAsState(initial = 0F)
    val endOffset by viewModel.endOffsetFlow.collectAsState(initial = 0F)

    Canvas(modifier.trimmedZoneModifier(viewModel)) {
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
private fun Modifier.trimmedZoneModifier(viewModel: TrimmerViewModel) =
    this
        .graphicsLayer(alpha = DEFAULT_GRAPHICS_LAYER_ALPHA)
        .playbackPointerInput(viewModel)

@Composable
private fun Modifier.playbackPointerInput(viewModel: TrimmerViewModel): Modifier {
    val isPlaying by viewModel.isPlayingState.collectAsState()
    return if (isPlaying) this.playbackModifier(viewModel) else this
}

@Composable
private fun Modifier.playbackModifier(viewModel: TrimmerViewModel): Modifier {
    val positionBroadcast = LocalTrimmerPositionBroadcast.current
    val durationMillis by viewModel.trackDurationInMillisFlow.collectAsState(initial = 0L)
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