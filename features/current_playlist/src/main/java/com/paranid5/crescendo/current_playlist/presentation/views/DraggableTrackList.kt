package com.paranid5.crescendo.current_playlist.presentation.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.ui.drag.DraggableList
import com.paranid5.crescendo.ui.drag.DraggableListItemView
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun <T : Track> DraggableTrackList(
    tracks: ImmutableList<T>,
    currentTrackIndex: Int,
    onTrackDismissed: (index: Int, item: T) -> Boolean,
    onTrackDragged: suspend (draggedItems: ImmutableList<T>, dragIndex: Int) -> Unit,
    trackItemView: DraggableListItemView<T>,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    bottomPadding: Dp = 16.dp,
) = DraggableList(
    items = tracks,
    currentItemIndex = currentTrackIndex,
    onDismissed = onTrackDismissed,
    onDragged = onTrackDragged,
    itemView = trackItemView,
    modifier = modifier,
    itemModifier = trackItemModifier,
    bottomPadding = bottomPadding,
    key = { index, track -> "${track.hashCode()}$index" }
)

@Composable
internal fun <T : Track> DraggableTrackList(
    tracks: ImmutableList<T>,
    currentTrackIndex: Int,
    onTrackDismissed: (index: Int, item: T) -> Boolean,
    onTrackDragged: suspend (draggedItems: ImmutableList<T>, dragIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    bottomPadding: Dp = 16.dp,
) = DraggableTrackList(
    tracks = tracks,
    currentTrackIndex = currentTrackIndex,
    onTrackDismissed = onTrackDismissed,
    onTrackDragged = onTrackDragged,
    modifier = modifier,
    trackItemModifier = trackItemModifier,
    bottomPadding = bottomPadding,
    trackItemView = { trackList, trackInd, currentTrackDragIndex, trackModifier ->
        DraggableTrackItem(
            tracks = trackList,
            trackIndex = trackInd,
            currentTrackDragIndex = currentTrackDragIndex,
            modifier = trackModifier
        )
    }
)