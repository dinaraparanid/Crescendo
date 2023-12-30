package com.paranid5.crescendo.presentation.main.tracks.views

import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.presentation.main.tracks.views.properties.AddToCurrentPlaylistProperty
import com.paranid5.crescendo.presentation.main.tracks.views.properties.TrimTrackProperty
import com.paranid5.crescendo.services.track_service.TrackServiceAccessor

@Composable
fun TrackPropertiesMenu(
    track: Track,
    isPropertiesMenuShownState: MutableState<Boolean>,
    storageHandler: StorageHandler,
    trackServiceAccessor: TrackServiceAccessor,
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
            storageHandler = storageHandler,
            trackServiceAccessor = trackServiceAccessor
        )

        TrimTrackProperty(
            track = track,
            modifier = itemModifier
        )
    }
}