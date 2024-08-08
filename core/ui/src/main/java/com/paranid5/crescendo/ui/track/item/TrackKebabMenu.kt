package com.paranid5.crescendo.ui.track.item

import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.ui.track.item.properties.AddToCurrentPlaylistProperty
import com.paranid5.crescendo.ui.track.item.properties.TrimTrackProperty
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState

@Composable
internal fun TrackKebabMenu(
    track: TrackUiState,
    isPropertiesMenuShownState: MutableState<Boolean>,
    navigateToTrimmer: (trackUri: String) -> Unit,
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
            trackPath = track.path,
            modifier = itemModifier,
            navigateToTrimmer = navigateToTrimmer,
        )
    }
}
