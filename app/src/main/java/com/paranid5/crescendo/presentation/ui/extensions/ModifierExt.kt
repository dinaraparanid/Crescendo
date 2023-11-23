package com.paranid5.crescendo.presentation.ui.extensions

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

fun Modifier.simpleShadow(elevation: Dp = 60.dp, color: Color? = null) = this.composed {
    val colors = LocalAppColors.current.value

    shadow(
        elevation = elevation,
        shape = RoundedCornerShape(20.dp),
        ambientColor = color ?: colors.primary,
        spotColor = color ?: colors.primary
    )
}