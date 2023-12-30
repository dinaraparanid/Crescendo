package com.paranid5.crescendo.presentation.main.trimmer.views.waveform

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.composition_locals.trimmer.LocalTrimmerPositionBroadcast
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_HEIGHT_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.DEFAULT_GRAPHICS_LAYER_ALPHA
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectEndOffsetAsState
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectIsPlayingAsState
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectStartOffsetAsState
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectTrackDurationInMillisAsState
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@Composable
fun TrimmedZone(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinActivityViewModel(),
) {
    val colors = LocalAppColors.current
    val progressBrush = SolidColor(colors.primary.copy(alpha = 0.25F))

    val startOffset by viewModel.collectStartOffsetAsState()
    val endOffset by viewModel.collectEndOffsetAsState()

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
    viewModel: TrimmerViewModel = koinActivityViewModel(),
): Modifier {
    val isPlaying by viewModel.collectIsPlayingAsState()
    return if (isPlaying) playbackModifier() else this
}

@Composable
private fun Modifier.playbackModifier(
    viewModel: TrimmerViewModel = koinActivityViewModel(),
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