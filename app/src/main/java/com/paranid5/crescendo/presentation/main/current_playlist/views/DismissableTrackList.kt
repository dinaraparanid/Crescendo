package com.paranid5.crescendo.presentation.main.current_playlist.views

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.presentation.main.tracks.views.DefaultTrackItem
import com.paranid5.crescendo.presentation.ui.utils.drag.DismissableList
import com.paranid5.crescendo.presentation.ui.utils.drag.ListItemView
import kotlinx.collections.immutable.ImmutableList

@Composable
internal inline fun <T : Track> DismissableTrackList(
    tracks: ImmutableList<T>,
    scrollingState: LazyListState,
    crossinline onDismissed: (Int, T) -> Boolean,
    crossinline trackItemView: ListItemView<T>,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
) = DismissableList(
    items = tracks,
    scrollingState = scrollingState,
    modifier = modifier,
    itemView = { trackList, trackInd, itemModifier ->
        trackItemView(
            trackList,
            trackInd,
            itemModifier then trackItemModifier
        )
    },
    onDismissed = onDismissed
)

@Composable
internal inline fun <T : Track> DismissableTrackList(
    tracks: ImmutableList<T>,
    scrollingState: LazyListState,
    crossinline onDismissed: (Int, T) -> Boolean,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
) = DismissableList(
    items = tracks,
    scrollingState = scrollingState,
    modifier = modifier,
    itemView = { trackList, trackInd, itemModifier ->
        DefaultTrackItem(
            tracks = trackList,
            trackInd = trackInd,
            modifier = itemModifier then trackItemModifier,
        )
    },
    onDismissed = onDismissed
)