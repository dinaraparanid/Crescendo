package com.paranid5.crescendo.trimmer.presentation.ui.waveform

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_ARROW_CORNER_BACK_OFFSET
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_ARROW_CORNER_FRONT_OFFSET
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_ARROW_CORNER_OFFSET
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_CIRCLE_CENTER
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_CIRCLE_RADIUS
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_RECT_OFFSET
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_RECT_WIDTH
import com.paranid5.crescendo.trimmer.presentation.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.trimmer.presentation.effects.waveform.RequestStartBorderFocusEffect
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent

@Composable
internal fun StartBorder(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO,
) {
    val appColors = colors
    val progressBrush = remember(appColors) { SolidColor(appColors.primary) }
    val iconBrush = remember(appColors) { SolidColor(appColors.text.primary) }

    Canvas(
        modifier.startBorderHorizontalDrag(
            state = state,
            onUiIntent = onUiIntent,
            spikeWidthRatio = spikeWidthRatio,
        )
    ) {
        drawBorder(progressBrush)

        drawStartTouchPad(
            progressBrush = progressBrush,
            iconBrush = iconBrush
        )
    }
}

@Composable
private fun Modifier.startBorderHorizontalDrag(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    spikeWidthRatio: Int,
): Modifier {
    val isDraggedState = remember { mutableStateOf(false) }
    val isPositionedState = remember { mutableStateOf(false) }

    RequestStartBorderFocusEffect(
        isDraggedState = isDraggedState,
        isPositionedState = isPositionedState
    )

    return this.startBorderDragInput(
        state = state,
        onUiIntent = onUiIntent,
        isDraggedState = isDraggedState,
        isPositionedState = isPositionedState,
        spikeWidthRatio = spikeWidthRatio,
    )
}

@Composable
private fun Modifier.startBorderDragInput(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    isDraggedState: MutableState<Boolean>,
    isPositionedState: MutableState<Boolean>,
    spikeWidthRatio: Int,
): Modifier {
    val startMillis = remember(state.playbackPositions.startPosInMillis) {
        state.playbackPositions.startPosInMillis
    }

    val endMillis = remember(state.playbackPositions.endPosInMillis) {
        state.playbackPositions.endPosInMillis
    }

    var isDragged by isDraggedState
    var isPositioned by isPositionedState

    return this
        .onGloballyPositioned { isPositioned = true }
        .pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragEnd = { isDragged = true },
            ) { change, dragAmount ->
                change.consume()
                onUiIntent(
                    TrimmerUiIntent.Positions.UpdateStartPosition(
                        (startMillis + (dragAmount * 200 / spikeWidthRatio))
                            .toLong()
                            .coerceIn(0 until endMillis)
                    )
                )
            }
        }
}

internal fun StartBorderOffset(startOffset: Float, waveformWidth: Int) =
    (CONTROLLER_CIRCLE_CENTER / 2 +
            startOffset * (waveformWidth - CONTROLLER_CIRCLE_RADIUS - CONTROLLER_RECT_OFFSET)).toInt()

private fun DrawScope.drawBorder(progressBrush: SolidColor) =
    drawRoundRect(
        brush = progressBrush,
        topLeft = Offset(CONTROLLER_CIRCLE_CENTER - CONTROLLER_RECT_OFFSET, 0F),
        size = Size(
            width = CONTROLLER_RECT_WIDTH,
            height = size.height
        ),
        cornerRadius = CornerRadius(10F, 10F),
        style = Fill,
    )

internal fun DrawScope.drawStartTouchPad(progressBrush: SolidColor, iconBrush: SolidColor) {
    drawTouchPadCircle(progressBrush)
    drawTouchPadIcon(iconBrush)
}

private fun DrawScope.drawTouchPadCircle(brush: SolidColor) =
    drawCircle(
        brush = brush,
        radius = CONTROLLER_CIRCLE_RADIUS,
        center = Offset(CONTROLLER_CIRCLE_CENTER, size.height - CONTROLLER_CIRCLE_CENTER),
    )

private fun DrawScope.drawTouchPadIcon(brush: SolidColor) =
    drawPath(
        path = IconPath(size),
        brush = brush,
        style = Fill,
    )

private fun IconPath(size: Size) = Path().apply {
    moveTo(
        x = CONTROLLER_CIRCLE_CENTER + CONTROLLER_ARROW_CORNER_BACK_OFFSET,
        y = size.height - CONTROLLER_CIRCLE_CENTER - CONTROLLER_ARROW_CORNER_OFFSET,
    )

    lineTo(
        x = CONTROLLER_CIRCLE_CENTER - CONTROLLER_ARROW_CORNER_FRONT_OFFSET,
        y = size.height - CONTROLLER_CIRCLE_CENTER,
    )

    lineTo(
        x = CONTROLLER_CIRCLE_CENTER + CONTROLLER_ARROW_CORNER_BACK_OFFSET,
        y = size.height - CONTROLLER_CIRCLE_CENTER + CONTROLLER_ARROW_CORNER_OFFSET,
    )

    close()
}