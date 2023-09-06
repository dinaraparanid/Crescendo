package com.paranid5.crescendo.presentation.tracks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.data.tracks.Track
import com.paranid5.crescendo.domain.StorageHandler
import com.paranid5.crescendo.domain.services.track_service.TrackServiceAccessor
import kotlinx.coroutines.CoroutineScope
import org.koin.compose.koinInject

typealias TrackItemView = @Composable (
    tracks: List<Track>,
    trackInd: Int,
    scope: CoroutineScope,
    storageHandler: StorageHandler,
    trackServiceAccessor: TrackServiceAccessor,
    modifier: Modifier
) -> Unit

@Composable
fun TrackList(
    tracks: List<Track>,
    scrollingState: LazyListState,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    trackItemView: TrackItemView,
    storageHandler: StorageHandler = koinInject(),
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) {
    val scope = rememberCoroutineScope()

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
                scope,
                storageHandler,
                trackServiceAccessor,
                trackItemModifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TrackList(
    tracks: List<Track>,
    scrollingState: LazyListState,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject(),
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) = TrackList(
    tracks = tracks,
    scrollingState = scrollingState,
    modifier = modifier,
    trackItemView = { _, trackInd, scope, _, _, trackModifier ->
        DefaultTrackItem(
            modifier = trackModifier.then(trackItemModifier),
            tracks = tracks,
            trackInd = trackInd,
            scope = scope,
            storageHandler = storageHandler,
            trackServiceAccessor = trackServiceAccessor,
        )
    },
    storageHandler = storageHandler,
    trackServiceAccessor = trackServiceAccessor
)