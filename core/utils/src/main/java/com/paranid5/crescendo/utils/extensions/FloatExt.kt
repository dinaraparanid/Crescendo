package com.paranid5.crescendo.utils.extensions

fun Float.toPlaybackPosition(millisInPercentage: Float) = (this * millisInPercentage).toLong()

fun Float.toAngle(
    valueRange: ClosedFloatingPointRange<Float>,
    angleRange: ClosedFloatingPointRange<Float>
): Float {
    val valuePercentage = (this - valueRange.start) /
            (valueRange.endInclusive - valueRange.start) * 100

    return (angleRange.endInclusive - angleRange.start) / 100 * valuePercentage + angleRange.start
}