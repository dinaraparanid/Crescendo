package com.paranid5.crescendo.presentation.main.tracks.views

import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.presentation.main.tracks.views.properties.AddToCurrentPlaylistProperty
import com.paranid5.crescendo.presentation.main.tracks.views.properties.TrimTrackProperty

@Composable
fun TrackPropertiesMenu(
    track: com.paranid5.crescendo.core.common.tracks.Track,
    isPropertiesMenuShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier
) {
    var isPropertiesMenuShown by isPropertiesMenuShownState

    DropdownMenu(
        expanded = isPropertiesMenuShown,
        onDismissRequest = { isPropertiesMenuShown = false },
        modifier = modifier
    ) {
        AddToCurrentPlaylistProperty(
            track = track,
            modifier = itemModifier,
        )

        TrimTrackProperty(
            track = track,
            modifier = itemModifier
        )
    }
}