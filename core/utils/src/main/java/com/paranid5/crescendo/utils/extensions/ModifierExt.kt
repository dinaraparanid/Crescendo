package com.paranid5.crescendo.utils.extensions

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions

private val DEFAULT_ELEVATION = 60.dp

fun Modifier.simpleShadow(
    elevation: Dp = DEFAULT_ELEVATION,
    color: Color? = null,
) = this.composed {
    shadow(
        elevation = elevation,
        shape = RoundedCornerShape(dimensions.corners.medium),
        ambientColor = color ?: colors.primary,
        spotColor = color ?: colors.primary,
    )
}
