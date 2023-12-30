package com.paranid5.crescendo.presentation.main.tracks.views

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
import com.paranid5.crescendo.R
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.services.track_service.TrackServiceAccessor
import org.koin.compose.koinInject

@Composable
fun TrackPropertiesButton(
    track: Track,
    tint: Color,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject(),
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) {
    val isPropertiesMenuShownState = remember { mutableStateOf(false) }

    Box(modifier) {
        TrackPropertiesMenu(
            track = track,
            isPropertiesMenuShownState = isPropertiesMenuShownState,
            itemModifier = itemModifier,
            storageHandler = storageHandler,
            trackServiceAccessor = trackServiceAccessor,
        )

        TrackPropertiesButtonImpl(
            tint = tint,
            isPropertiesMenuShownState = isPropertiesMenuShownState,
            modifier = iconModifier
        )
    }
}

@Composable
private fun TrackPropertiesButtonImpl(
    tint: Color,
    isPropertiesMenuShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    var isPropertiesMenuShown by isPropertiesMenuShownState

    IconButton(
        modifier = modifier,
        onClick = { isPropertiesMenuShown = true }
    ) {
        Icon(
            modifier = modifier,
            painter = painterResource(R.drawable.three_dots),
            contentDescription = stringResource(R.string.settings),
            tint = tint
        )
    }
}