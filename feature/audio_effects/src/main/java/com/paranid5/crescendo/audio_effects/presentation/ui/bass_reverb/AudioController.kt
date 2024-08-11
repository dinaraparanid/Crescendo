package com.paranid5.crescendo.audio_effects.presentation.ui.bass_reverb

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.paranid5.crescendo.utils.extensions.toAngle

private const val FullArcAngle = 270

@Composable
internal fun AudioController(
    value: Float,
    contentDescription: String,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0F..1F,
    angleRange: ClosedFloatingPointRange<Float> = ControllerMinAngle..ControllerMaxAngle,
    onValueChange: (Float) -> Unit,
) {
    val inputValueState = remember {
        mutableFloatStateOf(value)
    }

    val rotationAngleState = remember {
        mutableFloatStateOf(value.toAngle(valueRange, angleRange))
    }

    val rotationAngle by rotationAngleState

    val angleRangeDistance by rememberAngleRangeDistance(angleRange)
    val fixedRotationAngle by rememberFixedRotationAngle(rotationAngle, angleRangeDistance)
    val arcStartAngle by rememberArcStartAngle(angleRange)
    val anglePercent by rememberAnglePercent(angleRangeDistance)
    val valuePercent by rememberValuePercent(valueRange)

    val touchXState = remember { mutableFloatStateOf(0F) }
    val touchYState = remember { mutableFloatStateOf(0F) }
    val touchOffsetState = remember { mutableStateOf(Offset.Zero) }

    val centerXState = remember { mutableFloatStateOf(0F) }
    val centerYState = remember { mutableFloatStateOf(0F) }

    AudioControllerContent(
        angleRangeDistance = angleRangeDistance,
        fixedRotationAngle = fixedRotationAngle,
        arcStartAngle = arcStartAngle,
        anglePercent = anglePercent,
        valuePercent = valuePercent,
        contentDescription = contentDescription,
        inputValueState = inputValueState,
        rotationAngleState = rotationAngleState,
        touchXState = touchXState,
        touchYState = touchYState,
        touchOffsetState = touchOffsetState,
        centerXState = centerXState,
        centerYState = centerYState,
        angleRange = angleRange,
        onValueChange = onValueChange,
        modifier = modifier,
    )
}

@Composable
private fun rememberAngleRangeDistance(angleRange: ClosedFloatingPointRange<Float>) =
    remember(angleRange) { derivedStateOf { angleRange.endInclusive - angleRange.start } }

@Composable
private fun rememberFixedRotationAngle(
    rotationAngle: Float,
    angleRangeDistance: Float,
) = remember(rotationAngle) {
    derivedStateOf { rotationAngle + angleRangeDistance / 2 }
}

@Composable
private fun rememberArcStartAngle(angleRange: ClosedFloatingPointRange<Float>) =
    remember(angleRange.start) { derivedStateOf { angleRange.start + FullArcAngle } }

@Composable
private fun rememberAnglePercent(angleRangeDistance: Float) =
    remember(angleRangeDistance) { derivedStateOf { angleRangeDistance / 100 } }

@Composable
private fun rememberValuePercent(valueRange: ClosedFloatingPointRange<Float>) =
    remember(valueRange) { derivedStateOf { (valueRange.endInclusive - valueRange.start) / 100 } }
