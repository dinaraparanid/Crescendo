package com.paranid5.crescendo.presentation.main.current_playlist.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.presentation.main.current_playlist.CurrentPlaylistViewModel
import com.paranid5.crescendo.presentation.main.current_playlist.properties.compose.collectCurrentTrackIndexAsState
import com.paranid5.crescendo.presentation.ui.utils.drag.DraggableList
import com.paranid5.crescendo.presentation.ui.utils.drag.DraggableListItemView
import kotlinx.collections.immutable.ImmutableList

@Composable
internal inline fun <T : Track> DraggableTrackList(
    viewModel: CurrentPlaylistViewModel,
    tracks: ImmutableList<T>,
    crossinline onTrackDismissed: (Int, T) -> Boolean,
    crossinline onTrackDragged: suspend (ImmutableList<T>, Int) -> Unit,
    crossinline trackItemView: DraggableListItemView<T>,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
) {
    val currentTrackIndex by viewModel.collectCurrentTrackIndexAsState()

    DraggableList(
        items = tracks,
        currentItemIndex = currentTrackIndex,
        onDismissed = onTrackDismissed,
        onDragged = onTrackDragged,
        itemView = trackItemView,
        modifier = modifier,
        itemModifier = trackItemModifier,
        key = { index, track -> track.hashCode() + index }
    )
}

@Composable
internal inline fun <T : Track> DraggableTrackList(
    viewModel: CurrentPlaylistViewModel,
    tracks: ImmutableList<T>,
    crossinline onTrackDismissed: (Int, T) -> Boolean,
    crossinline onTrackDragged: suspend (ImmutableList<T>, Int) -> Unit,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
) = DraggableTrackList(
    tracks = tracks,
    viewModel = viewModel,
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