package com.paranid5.crescendo.ui.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions

private val DefaultPushUpWidth = 35.dp
private val DefaultPushUpHeight = 4.dp

@Composable
fun PushUpButton(
    alpha: Float,
    modifier: Modifier = Modifier,
    width: Dp = DefaultPushUpWidth,
    height: Dp = DefaultPushUpHeight,
) {
    val visibleAlpha = remember(alpha) { 0.25F + alpha / 2 }
    val color = colors.utils.transparentUtility.copy(alpha = visibleAlpha)

    Canvas(
        onDraw = { drawRect(color) },
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(dimensions.padding.medium)),
    )
}
