package com.paranid5.crescendo.feature.playing.presentation.ui.kebab

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.paranid5.crescendo.core.resources.R

@Composable
internal fun VideoKebabMenuButton(
    tint: Color,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
) = IconButton(
    modifier = modifier,
    onClick = { /** TODO: Video properties */ }
) {
    KebabIcon(tint = tint, modifier = iconModifier)
}

@Composable
private fun KebabIcon(tint: Color, modifier: Modifier = Modifier) =
    Icon(
        painter = painterResource(R.drawable.ic_kebab_menu),
        contentDescription = null,
        tint = tint,
        modifier = modifier,
    )
