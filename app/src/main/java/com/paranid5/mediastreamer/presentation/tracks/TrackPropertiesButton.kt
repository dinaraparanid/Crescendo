package com.paranid5.mediastreamer.presentation.tracks

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.paranid5.mediastreamer.R

@Composable
fun TrackPropertiesButton(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    tint: Color = Color.White
) = IconButton(
    modifier = modifier,
    onClick = { /** TODO: Track settings */ }
) {
    Icon(
        modifier = iconModifier,
        painter = painterResource(R.drawable.three_dots),
        contentDescription = stringResource(R.string.settings),
        tint = tint
    )
}