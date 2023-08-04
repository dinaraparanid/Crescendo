package com.paranid5.mediastreamer.presentation.tracks

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.data.tracks.Track
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.domain.services.track_service.TrackServiceAccessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
                tracks = tracks,
                trackInd = ind,
                scope = scope,
                storageHandler = storageHandler,
                trackServiceAccessor = trackServiceAccessor,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TrackList(
    tracks: List<Track>,
    scrollingState: LazyListState,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject(),
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) = TrackList(
    tracks = tracks,
    scrollingState = scrollingState,
    modifier = modifier,
    trackItemView = { _, trackInd, scope, _, _, modifier ->
        DefaultTrackItem(
            tracks,
            trackInd,
            scope,
            storageHandler,
            trackServiceAccessor,
            modifier
        )
    },
    storageHandler = storageHandler,
    trackServiceAccessor = trackServiceAccessor
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissableTrackList(
    tracks: List<Track>,
    scrollingState: LazyListState,
    modifier: Modifier = Modifier,
    trackItemView: TrackItemView,
    storageHandler: StorageHandler = koinInject(),
    trackServiceAccessor: TrackServiceAccessor = koinInject(),
    onTrackDismiss: suspend (Int, Track) -> Unit
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
        ) { ind, track ->
            val dismissState = rememberDismissState(
                confirmValueChange = {
                    if (it == DismissValue.DismissedToEnd) {
                        Log.d("TrackList", "Track $track is removed from the current playlist")
                        scope.launch { onTrackDismiss(ind, track) }
                    }

                    it != DismissValue.DismissedToEnd
                }
            )

            SwipeToDismiss(
                state = dismissState,
                background = {},
                dismissContent = {
                    trackItemView(
                        tracks = tracks,
                        trackInd = ind,
                        scope = scope,
                        storageHandler = storageHandler,
                        trackServiceAccessor = trackServiceAccessor,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }
    }
}

@Composable
fun DismissableTrackList(
    tracks: List<Track>,
    scrollingState: LazyListState,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject(),
    trackServiceAccessor: TrackServiceAccessor = koinInject(),
    onTrackDismiss: suspend (Int, Track) -> Unit
) = DismissableTrackList(
    tracks = tracks,
    scrollingState = scrollingState,
    modifier = modifier,
    trackItemView = { _, trackInd, scope, _, _, modifier ->
        DefaultTrackItem(
            tracks,
            trackInd,
            scope,
            storageHandler,
            trackServiceAccessor,
            modifier
        )
    },
    storageHandler = storageHandler,
    trackServiceAccessor = trackServiceAccessor,
    onTrackDismiss = onTrackDismiss
)