package com.paranid5.crescendo.presentation.main.trimmer.views.waveform

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_ARROW_CORNER_BACK_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_ARROW_CORNER_FRONT_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_ARROW_CORNER_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_CIRCLE_CENTER
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_CIRCLE_RADIUS
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_RECT_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_RECT_WIDTH
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.presentation.main.trimmer.properties.durationInMillisFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.endPosInMillisState
import com.paranid5.crescendo.presentation.main.trimmer.properties.setEndPosInMillis
import com.paranid5.crescendo.presentation.main.trimmer.properties.startPosInMillisState
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

    val startMillis by viewModel.startPosInMillisState.collectAsState()
    val endMillis by viewModel.endPosInMillisState.collectAsState()
    val durationInMillis by viewModel.durationInMillisFlow.collectAsState(initial = 0L)

    Canvas(
        modifier.pointerInput(Unit) {
            detectHorizontalDragGestures { change, dragAmount ->
                change.consume()
                viewModel.setEndPosInMillis(
                    (endMillis + (dragAmount * 200 / spikeWidthRatio))
                        .toLong()
                        .coerceIn(startMillis + 1..durationInMillis)
                )
            }
        }
    ) {
        drawBorder(progressBrush)

        drawToucher(
            progressBrush = progressBrush,
            iconBrush = iconBrush
        )
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

private fun DrawScope.drawToucher(progressBrush: SolidColor, iconBrush: SolidColor) {
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