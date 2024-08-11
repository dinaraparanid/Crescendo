package com.paranid5.crescendo.audio_effects.presentation.ui.bass_reverb

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import kotlin.math.PI
import kotlin.math.atan2

private val ProgressBarSize = 85.dp

@Composable
internal fun AudioControllerContent(
    angleRangeDistance: Float,
    fixedRotationAngle: Float,
    arcStartAngle: Float,
    anglePercent: Float,
    valuePercent: Float,
    contentDescription: String,
    angleRange: ClosedFloatingPointRange<Float>,
    inputValueState: MutableState<Float>,
    rotationAngleState: MutableState<Float>,
    touchXState: MutableState<Float>,
    touchYState: MutableState<Float>,
    touchOffsetState: MutableState<Offset>,
    centerXState: MutableState<Float>,
    centerYState: MutableState<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) = Box(modifier) {
    ProgressBar(
        arcStartAngle = arcStartAngle,
        angleRangeDistance = angleRangeDistance,
        fixedRotationAngle = fixedRotationAngle,
        modifier = Modifier
            .size(ProgressBarSize)
            .align(Alignment.Center),
    )

    Image(
        painter = painterResource(R.drawable.audio_controller),
        contentDescription = contentDescription,
        modifier = Modifier
            .align(Alignment.Center)
            .controllerModifier(
                anglePercent = anglePercent,
                valuePercent = valuePercent,
                inputValueState = inputValueState,
                rotationAngleState = rotationAngleState,
                touchXState = touchXState,
                touchYState = touchYState,
                touchOffsetState = touchOffsetState,
                centerXState = centerXState,
                centerYState = centerYState,
                angleRange = angleRange,
                onValueChange = onValueChange,
            ),
    )
}

@Composable
private fun ProgressBar(
    arcStartAngle: Float,
    angleRangeDistance: Float,
    fixedRotationAngle: Float,
    modifier: Modifier = Modifier,
) {
    val colors = colors

    Canvas(modifier) {
        drawFullBarArc(
            color = colors.utils.disabled,
            arcStartAngle = arcStartAngle,
            angleRangeDistance = angleRangeDistance,
        )

        drawProgressBarArc(
            color = colors.primary,
            arcStartAngle = arcStartAngle,
            fixedRotationAngle = fixedRotationAngle,
        )
    }
}

@Composable
private fun Modifier.controllerModifier(
    anglePercent: Float,
    valuePercent: Float,
    inputValueState: MutableState<Float>,
    rotationAngleState: MutableState<Float>,
    touchXState: MutableState<Float>,
    touchYState: MutableState<Float>,
    touchOffsetState: MutableState<Offset>,
    centerXState: MutableState<Float>,
    centerYState: MutableState<Float>,
    angleRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
): Modifier {
    val rotationAngle by rotationAngleState
    val centerX by centerXState
    val centerY by centerYState

    return this
        .centerPositionProvider(
            centerXState = centerXState,
            centerYState = centerYState
        )
        .controllerDragInput(
            centerX = centerX,
            centerY = centerY,
            anglePercent = anglePercent,
            valuePercent = valuePercent,
            inputValueState = inputValueState,
            rotationAngleState = rotationAngleState,
            touchXState = touchXState,
            touchYState = touchYState,
            touchOffsetState = touchOffsetState,
            angleRange = angleRange,
            onValueChange = onValueChange
        )
        .rotate(rotationAngle)
}

@Composable
private fun Modifier.centerPositionProvider(
    centerXState: MutableState<Float>,
    centerYState: MutableState<Float>,
): Modifier {
    var centerX by centerXState
    var centerY by centerYState

    return this.onGloballyPositioned {
        val boundsInWindow = it.boundsInWindow()
        centerX = boundsInWindow.size.width / 2F
        centerY = boundsInWindow.size.height / 2F
    }
}

@Composable
private inline fun Modifier.controllerDragInput(
    centerX: Float,
    centerY: Float,
    anglePercent: Float,
    valuePercent: Float,
    angleRange: ClosedFloatingPointRange<Float>,
    inputValueState: MutableState<Float>,
    rotationAngleState: MutableState<Float>,
    touchXState: MutableState<Float>,
    touchYState: MutableState<Float>,
    touchOffsetState: MutableState<Offset>,
    crossinline onValueChange: (Float) -> Unit,
): Modifier {
    var inputValue by inputValueState
    var rotationAngle by rotationAngleState

    var touchX by touchXState
    var touchY by touchYState
    var touchOffset by touchOffsetState

    return this.pointerInput(Unit) {
        detectDragGestures { change, offset ->
            change.consume()
            val touchOffset2 = touchOffset + offset

            val degrees = -atan2(
                y = centerX - touchOffset2.x,
                x = centerY - touchOffset2.y
            ) * (180 / PI).toFloat()

            if (degrees !in angleRange)
                return@detectDragGestures

            touchOffset += offset
            touchX = touchOffset.x
            touchY = touchOffset.y

            val rotationPercents = (degrees - angleRange.start) / anglePercent
            rotationAngle = degrees
            inputValue = valuePercent * rotationPercents
            onValueChange(inputValue)
        }
    }
}

private fun DrawScope.drawFullBarArc(
    color: Color,
    arcStartAngle: Float,
    angleRangeDistance: Float,
) = drawArc(
    color = color,
    startAngle = arcStartAngle,
    sweepAngle = angleRangeDistance,
    useCenter = true,
)

private fun DrawScope.drawProgressBarArc(
    color: Color,
    arcStartAngle: Float,
    fixedRotationAngle: Float,
) = drawArc(
    color = color,
    startAngle = arcStartAngle,
    sweepAngle = fixedRotationAngle,
    useCenter = true,
)
