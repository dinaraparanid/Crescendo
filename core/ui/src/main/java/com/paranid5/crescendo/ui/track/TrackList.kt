package com.paranid5.crescendo.ui.track

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.ui.appbar.appBarHeight
import kotlinx.collections.immutable.ImmutableList

typealias TrackItemView = @Composable (
    tracks: ImmutableList<Track>,
    trackInd: Int,
    modifier: Modifier
) -> Unit

@Composable
fun TrackList(
    tracks: ImmutableList<Track>,
    scrollingState: LazyListState,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    trackItemView: TrackItemView,
    bottomPadding: Dp = 16.dp
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        state = scrollingState,
        modifier = modifier
    ) {
        itemsIndexed(
            items = tracks,
            key = { ind, track -> "${track.hashCode()}$ind" }
        ) { ind, _ ->
            trackItemView(
                tracks,
                ind,
                trackItemModifier.fillMaxWidth()
            )
        }

        item { Spacer(Modifier.height(bottomPadding)) }
    }
}