package com.paranid5.crescendo.presentation.main.current_playlist.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.utils.drag.DraggableList
import com.paranid5.crescendo.utils.drag.DraggableListItemView
import kotlinx.collections.immutable.ImmutableList

@Composable
fun <T : Track> DraggableTrackList(
    tracks: ImmutableList<T>,
    currentTrackIndex: Int,
    onTrackDismissed: (index: Int, item: T) -> Boolean,
    onTrackDragged: suspend (draggedItems: ImmutableList<T>, dragIndex: Int) -> Unit,
    trackItemView: DraggableListItemView<T>,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
) = DraggableList(
    items = tracks,
    currentItemIndex = currentTrackIndex,
    onDismissed = onTrackDismissed,
    onDragged = onTrackDragged,
    itemView = trackItemView,
    modifier = modifier,
    itemModifier = trackItemModifier,
    key = { index, track -> track.hashCode() + index }
)

@Composable
fun <T : Track> DraggableTrackList(
    tracks: ImmutableList<T>,
    currentTrackIndex: Int,
    onTrackDismissed: (index: Int, item: T) -> Boolean,
    onTrackDragged: suspend (draggedItems: ImmutableList<T>, dragIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
) = DraggableTrackList(
    tracks = tracks,
    currentTrackIndex = currentTrackIndex,
    onTrackDismissed = onTrackDismissed,
    onTrackDragged = onTrackDragged,
    modifier = modifier,
    trackItemModifier = trackItemModifier,
    trackItemView = { trackList, trackInd, currentTrackDragIndex, trackModifier ->
        DraggableTrackItem(
            tracks = trackList,
            trackIndex = trackInd,
            currentTrackDragIndex = currentTrackDragIndex,
            modifier = trackModifier
        )
    }
)