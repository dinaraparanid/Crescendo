package com.paranid5.crescendo.presentation.main.tracks.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.common.tracks.Track
import kotlinx.collections.immutable.ImmutableList

typealias TrackItemView = @Composable (
    tracks: ImmutableList<com.paranid5.crescendo.core.common.tracks.Track>,
    trackInd: Int,
    modifier: Modifier
) -> Unit

@Composable
fun TrackList(
    tracks: ImmutableList<com.paranid5.crescendo.core.common.tracks.Track>,
    scrollingState: LazyListState,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    trackItemView: TrackItemView,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        state = scrollingState,
        modifier = modifier
    ) {
        itemsIndexed(
            items = tracks,
            key = { ind, track -> track.path.hashCode() + ind }
        ) { ind, _ ->
            trackItemView(
                tracks,
                ind,
                trackItemModifier.fillMaxWidth()
            )
        }
    }
}