package com.paranid5.crescendo.ui.track.item

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.common.tracks.Track

@Composable
fun TrackPropertiesButton(
    track: Track,
    tint: Color,
    modifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
) {
    val isPropertiesMenuShownState = remember { mutableStateOf(false) }

    Box(modifier) {
        TrackPropertiesMenu(
            track = track,
            isPropertiesMenuShownState = isPropertiesMenuShownState,
            itemModifier = itemModifier,
        )

        TrackPropertiesButtonImpl(
            tint = tint,
            isPropertiesMenuShownState = isPropertiesMenuShownState,
            modifier = buttonModifier,
            iconModifier = iconModifier
        )
    }
}

@Composable
private fun TrackPropertiesButtonImpl(
    tint: Color,
    isPropertiesMenuShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier
) {
    var isPropertiesMenuShown by isPropertiesMenuShownState

    IconButton(
        modifier = modifier,
        onClick = { isPropertiesMenuShown = true }
    ) {
        PropertiesIcon(tint = tint, modifier = iconModifier)
    }
}

@Composable
private fun PropertiesIcon(tint: Color, modifier: Modifier = Modifier) =
    Icon(
        modifier = modifier,
        painter = painterResource(R.drawable.kebab_menu),
        contentDescription = stringResource(R.string.settings),
        tint = tint
    )