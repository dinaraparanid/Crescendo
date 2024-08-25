package com.paranid5.crescendo.trimmer.presentation.ui.waveform

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_HEIGHT_OFFSET
import com.paranid5.crescendo.trimmer.presentation.DEFAULT_GRAPHICS_LAYER_ALPHA
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent
import com.paranid5.crescendo.ui.utils.applyIf

@Composable
internal fun TrimmedZone(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val startOffset = remember(state.startOffset) { state.startOffset }
    val endOffset = remember(state.endOffset) { state.endOffset }

    val progressColor = colors.primary
    val progressBrush = remember(progressColor) {
        SolidColor(progressColor.copy(alpha = 0.25F))
    }

    Canvas(
        modifier.trimmedZoneModifier(
            state = state,
            onUiIntent = onUiIntent,
        )
    ) {
        drawRect(
            brush = progressBrush,
            topLeft = Offset(startOffset * size.width, 0F),
            size = Size(
                width = (endOffset - startOffset) * size.width,
                height = size.height - CONTROLLER_HEIGHT_OFFSET,
            ),
        )
    }
}

@Composable
private fun Modifier.trimmedZoneModifier(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
) = this
    .graphicsLayer(alpha = DEFAULT_GRAPHICS_LAYER_ALPHA)
    .playbackPointerInput(
        state = state,
        onUiIntent = onUiIntent,
    )

@Composable
private fun Modifier.playbackPointerInput(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
): Modifier {
    val isPlaying = remember(state.playbackProperties.isPlaying) {
        state.playbackProperties.isPlaying
    }

    return applyIf(isPlaying) { playbackModifier(state = state, onUiIntent = onUiIntent) }
}

@Composable
private fun Modifier.playbackModifier(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
): Modifier {
    val durationMillis = remember(state.trackDurationInMillis) {
        state.trackDurationInMillis
    }

    return this.pointerInput(Unit) {
        detectTapGestures { offset ->
            onUiIntent(
                TrimmerUiIntent.Positions.SeekTo(
                    playbackPosition(
                        offset = offset.y,
                        size = size.width,
                        durationMillis = durationMillis,
                    )
                )
            )
        }
    }
}

private fun playbackPosition(offset: Float, size: Int, durationMillis: Long) =
    (offset / size * durationMillis).toLong()