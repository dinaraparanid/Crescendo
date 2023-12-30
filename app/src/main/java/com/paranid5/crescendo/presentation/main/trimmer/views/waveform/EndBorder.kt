package com.paranid5.crescendo.presentation.main.trimmer.views.waveform

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
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_ARROW_CORNER_BACK_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_ARROW_CORNER_FRONT_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_ARROW_CORNER_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_CIRCLE_CENTER
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_CIRCLE_RADIUS
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_RECT_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_RECT_WIDTH
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.presentation.main.trimmer.effects.waveform.RequestEndBorderFocusEffect
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectEndPosInMillisAsState
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectStartPosInMillisAsState
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectTrackDurationInMillisAsState
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun EndBorder(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier,
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO,
) {
    val colors = LocalAppColors.current
    val progressBrush = SolidColor(colors.primary)
    val iconBrush = SolidColor(colors.fontColor)

    Canvas(modifier.endBorderHorizontalDrag(viewModel, spikeWidthRatio)) {
        drawBorder(progressBrush)

        drawEndToucher(
            progressBrush = progressBrush,
            iconBrush = iconBrush
        )
    }
}

@Composable
private fun Modifier.endBorderHorizontalDrag(
    viewModel: TrimmerViewModel,
    spikeWidthRatio: Int
): Modifier {
    val isDraggedState = remember { mutableStateOf(false) }
    val isPositionedState = remember { mutableStateOf(false) }

    RequestEndBorderFocusEffect(
        isDraggedState = isDraggedState,
        isPositionedState = isPositionedState
    )

    return this.endBorderDragInput(
        viewModel = viewModel,
        isDraggedState = isDraggedState,
        isPositionedState = isPositionedState,
        spikeWidthRatio = spikeWidthRatio
    )
}

@Composable
private fun Modifier.endBorderDragInput(
    viewModel: TrimmerViewModel,
    isDraggedState: MutableState<Boolean>,
    isPositionedState: MutableState<Boolean>,
    spikeWidthRatio: Int
): Modifier {
    val startMillis by viewModel.collectStartPosInMillisAsState()
    val endMillis by viewModel.collectEndPosInMillisAsState()
    val durationInMillis by viewModel.collectTrackDurationInMillisAsState()

    var isDragged by isDraggedState
    var isPositioned by isPositionedState

    return this
        .onGloballyPositioned { isPositioned = true }
        .pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragEnd = { isDragged = true }
            ) { change, dragAmount ->
                change.consume()
                viewModel.setEndPosInMillis(
                    (endMillis + (dragAmount * 200 / spikeWidthRatio))
                        .toLong()
                        .coerceIn(startMillis + 1..durationInMillis)
                )
            }
        }
}

fun EndBorderOffset(endOffset: Float, waveformWidth: Int) =
    (endOffset * waveformWidth +
            (1 - endOffset) * (CONTROLLER_CIRCLE_RADIUS + CONTROLLER_RECT_OFFSET) -
            CONTROLLER_CIRCLE_CENTER / 2).toInt()

private fun DrawScope.drawBorder(brush: SolidColor) =
    drawRoundRect(
        brush = brush,
        topLeft = Offset(-CONTROLLER_CIRCLE_CENTER - CONTROLLER_RECT_OFFSET, 0F),
        size = Size(
            width = CONTROLLER_RECT_WIDTH,
            height = size.height
        ),
        cornerRadius = CornerRadius(10F, 10F),
        style = Fill
    )

internal fun DrawScope.drawEndToucher(progressBrush: SolidColor, iconBrush: SolidColor) {
    drawToucherCircle(progressBrush)
    drawToucherIcon(iconBrush)
}

private fun DrawScope.drawToucherCircle(brush: SolidColor) =
    drawCircle(
        brush = brush,
        radius = CONTROLLER_CIRCLE_RADIUS,
        center = Offset(-CONTROLLER_CIRCLE_CENTER, size.height - CONTROLLER_CIRCLE_CENTER)
    )

private fun DrawScope.drawToucherIcon(brush: SolidColor) =
    drawPath(
        path = IconPath(size),
        brush = brush,
        style = Fill,
    )

private fun IconPath(size: Size) = Path().apply {
    moveTo(
        x = -CONTROLLER_CIRCLE_CENTER - CONTROLLER_ARROW_CORNER_BACK_OFFSET,
        y = size.height - CONTROLLER_CIRCLE_CENTER - CONTROLLER_ARROW_CORNER_OFFSET
    )

    lineTo(
        x = -CONTROLLER_CIRCLE_CENTER + CONTROLLER_ARROW_CORNER_FRONT_OFFSET,
        size.height - CONTROLLER_CIRCLE_CENTER
    )

    lineTo(
        x = -CONTROLLER_CIRCLE_CENTER - CONTROLLER_ARROW_CORNER_BACK_OFFSET,
        y = size.height - CONTROLLER_CIRCLE_CENTER + CONTROLLER_ARROW_CORNER_OFFSET
    )

    close()
}