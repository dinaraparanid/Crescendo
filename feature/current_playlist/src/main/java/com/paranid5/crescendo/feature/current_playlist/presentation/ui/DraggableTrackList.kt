package com.paranid5.crescendo.feature.current_playlist.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.ui.drag.DraggableList
import com.paranid5.crescendo.ui.drag.DraggableListItemContent
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun DraggableTrackList(
    tracks: ImmutableList<TrackUiState>,
    currentTrackIndex: Int,
    onTrackDismissed: (index: Int, item: TrackUiState) -> Boolean,
    onTrackDragged: (draggedItems: ImmutableList<TrackUiState>, dragIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    trackItemContent: DraggableListItemContent<TrackUiState>,
) = DraggableList(
    items = tracks,
    currentItemIndex = currentTrackIndex,
    onDismissed = onTrackDismissed,
    onDragged = onTrackDragged,
    itemContent = trackItemContent,
    modifier = modifier,
    itemModifier = trackItemModifier,
    key = { index, track -> "${track.hashCode()}$index" },
)

@Composable
internal fun DraggableTrackList(
    tracks: ImmutableList<TrackUiState>,
    currentTrackIndex: Int,
    onTrackDismissed: (index: Int, item: TrackUiState) -> Boolean,
    onTrackDragged: (draggedItems: ImmutableList<TrackUiState>, dragIndex: Int) -> Unit,
    onTrackClick: (index: Int) -> Unit,
    addToPlaylist: (track: Track) -> Unit,
    showTrimmer: (trackUri: String) -> Unit,
    showMetaEditor: () -> Unit,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
) = DraggableTrackList(
    tracks = tracks,
    currentTrackIndex = currentTrackIndex,
    onTrackDismissed = onTrackDismissed,
    onTrackDragged = onTrackDragged,
    modifier = modifier,
    trackItemModifier = trackItemModifier,
    trackItemContent = { trackList, trackInd, currentTrackDragIndex, trackModifier ->
        val isTrackCurrent by remember(trackInd, currentTrackDragIndex) {
            derivedStateOf { trackInd == currentTrackDragIndex }
        }

        DraggableTrackItem(
            tracks = trackList,
            trackIndex = trackInd,
            isCurrent = isTrackCurrent,
            modifier = trackModifier,
            addToPlaylist = addToPlaylist,
            showTrimmer = showTrimmer,
            showMetaEditor = showMetaEditor,
            onClick = { onTrackClick(trackInd) },
        )
    },
)
