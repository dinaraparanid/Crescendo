package com.paranid5.crescendo.utils.extensions

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val DefaultElevation = 60.dp // TODO: 4.dp
private val DefaultRadius = 12.dp

fun Modifier.simpleShadow(
    elevation: Dp = DefaultElevation,
    color: Color = Color.Black,
    radius: Dp = DefaultRadius,
) = this then Modifier.shadow(
    elevation = elevation,
    shape = RoundedCornerShape(radius),
    ambientColor = color,
    spotColor = color,
    clip = false,
)

fun Modifier.simpleShadow(
    shape: Shape,
    elevation: Dp = DefaultElevation,
    color: Color = Color.Black,
) = this then Modifier.shadow(
    elevation = elevation,
    shape = shape,
    ambientColor = color,
    spotColor = color,
    clip = false,
)
