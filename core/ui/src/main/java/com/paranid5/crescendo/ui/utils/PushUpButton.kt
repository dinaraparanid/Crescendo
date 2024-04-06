package com.paranid5.crescendo.ui.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.TransparentUtility

private val PUSH_UP_WIDTH = 35.dp
private val PUSH_UP_HEIGHT = 4.dp

@Composable
fun PushUpButton(
    alpha: Float,
    modifier: Modifier = Modifier,
    width: Dp = PUSH_UP_WIDTH,
    height: Dp = PUSH_UP_HEIGHT
) = Canvas(
    onDraw = { drawRect(TransparentUtility.copy(alpha = 0.25F + alpha / 2)) },
    modifier = modifier
        .width(width)
        .height(height)
        .clip(RoundedCornerShape(10.dp))
)