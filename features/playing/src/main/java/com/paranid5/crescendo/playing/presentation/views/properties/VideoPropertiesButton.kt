package com.paranid5.crescendo.playing.presentation.views.properties

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R

@Composable
internal fun VideoPropertiesButton(
    tint: Color,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
) = IconButton(
    modifier = modifier,
    onClick = { /** TODO: Video properties */ }
) {
    PropertiesIcon(tint = tint, modifier = iconModifier)
}

@Composable
private fun PropertiesIcon(tint: Color, modifier: Modifier = Modifier) =
    Icon(
        painter = painterResource(R.drawable.kebab_menu),
        contentDescription = stringResource(R.string.settings),
        tint = tint,
        modifier = modifier
    )