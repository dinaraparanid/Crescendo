package com.paranid5.crescendo.presentation.ui.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.TransparentUtility

@Composable
fun PushUpButton(alpha: Float, modifier: Modifier = Modifier) =
    Canvas(
        onDraw = { drawRect(TransparentUtility.copy(alpha = 0.25F + alpha / 2)) },
        modifier = modifier
            .width(35.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(10.dp))
    )