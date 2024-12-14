package com.paranid5.crescendo.ui.track

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import kotlinx.collections.immutable.ImmutableList

typealias TrackItemContent <T> = @Composable (
    tracks: ImmutableList<T>,
    trackInd: Int,
    modifier: Modifier
) -> Unit

@Composable
fun <T> AppTrackList(
    tracks: ImmutableList<T>,
    key: (index: Int, item: T) -> Any,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(dimensions.padding.medium),
    itemContent: TrackItemContent<T>,
) = LazyColumn(
    verticalArrangement = verticalArrangement,
    contentPadding = contentPadding,
    modifier = modifier,
) {
    itemsIndexed(
        items = tracks,
        key = key,
    ) { ind, _ ->
        itemContent(tracks, ind, trackItemModifier.fillMaxWidth())
    }
}
