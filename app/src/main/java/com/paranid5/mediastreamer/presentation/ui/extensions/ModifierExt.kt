package com.paranid5.mediastreamer.presentation.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors

@Composable
fun Modifier.primaryColorShadow(elevation: Dp = 80.dp): Modifier {
    val colors = LocalAppColors.current.value

    return shadow(
        elevation = elevation,
        shape = RectangleShape,
        ambientColor = colors.primary,
        spotColor = colors.primary
    )
}