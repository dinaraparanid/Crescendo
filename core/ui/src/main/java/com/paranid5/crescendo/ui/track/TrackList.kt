package com.paranid5.crescendo.ui.track

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import kotlinx.collections.immutable.ImmutableList

typealias TrackItemContent = @Composable (
    tracks: ImmutableList<TrackUiState>,
    trackInd: Int,
    modifier: Modifier
) -> Unit

@Composable
fun TrackList(
    tracks: ImmutableList<TrackUiState>,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    bottomPadding: Dp = dimensions.padding.extraMedium,
    trackItemContent: TrackItemContent,
) = LazyColumn(
    verticalArrangement = Arrangement.spacedBy(dimensions.padding.medium),
    modifier = modifier,
) {
    itemsIndexed(
        items = tracks,
        key = { ind, track -> "${track.hashCode()}$ind" }
    ) { ind, _ ->
        trackItemContent(tracks, ind, trackItemModifier.fillMaxWidth())
    }

    item { Spacer(Modifier.height(bottomPadding)) }
}
