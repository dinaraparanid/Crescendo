package com.paranid5.crescendo.ui.track.item

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.ui.track.item.properties.AddToCurrentPlaylistProperty
import com.paranid5.crescendo.ui.track.item.properties.ChangeTrackMetaProperty
import com.paranid5.crescendo.ui.track.item.properties.TrimTrackProperty
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState

@Composable
internal fun TrackKebabMenu(
    track: TrackUiState,
    isPropertiesMenuShownState: MutableState<Boolean>,
    addToPlaylist: (track: Track) -> Unit,
    showTrimmer: (trackUri: String) -> Unit,
    showMetaEditor: () -> Unit,
    modifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
) {
    var isPropertiesMenuShown by isPropertiesMenuShownState

    DropdownMenu(
        expanded = isPropertiesMenuShown,
        onDismissRequest = { isPropertiesMenuShown = false },
        modifier = modifier.background(colors.background.highContrast),
    ) {
        AddToCurrentPlaylistProperty(
            track = track,
            modifier = itemModifier,
            addToPlaylist = addToPlaylist,
        )

        ChangeTrackMetaProperty(
            modifier = itemModifier,
            showMetaEditor = showMetaEditor,
        )

        TrimTrackProperty(
            trackPath = track.path,
            modifier = itemModifier,
            showTrimmer = showTrimmer,
        )
    }
}
