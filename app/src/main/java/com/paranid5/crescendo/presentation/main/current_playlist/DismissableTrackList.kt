package com.paranid5.crescendo.presentation.main.current_playlist

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
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.presentation.main.tracks.DefaultTrackItem
import com.paranid5.crescendo.presentation.main.tracks.TrackItemView
import com.paranid5.crescendo.services.track_service.TrackServiceAccessor
import kotlinx.coroutines.runBlocking
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal inline fun DismissableTrackList(
    tracks: List<Track>,
    scrollingState: LazyListState,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    crossinline onTrackDismissed: suspend (Int, Track) -> Boolean,
    storageHandler: StorageHandler = koinInject(),
    trackServiceAccessor: TrackServiceAccessor = koinInject(),
    crossinline trackItemView: TrackItemView,
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
                    when (it) {
                        DismissValue.DismissedToEnd -> {
                            Log.d("TrackList", "Track $track is removed from the current playlist")
                            runBlocking { onTrackDismissed(ind, track) }
                        }

                        else -> false
                    }
                }
            )

            SwipeToDismiss(
                state = dismissState,
                background = {},
                dismissContent = {
                    trackItemView(
                        tracks,
                        ind,
                        scope,
                        storageHandler,
                        trackServiceAccessor,
                        trackItemModifier.fillMaxWidth()
                    )
                }
            )
        }
    }
}

@Composable
internal inline fun DismissableTrackList(
    tracks: List<Track>,
    scrollingState: LazyListState,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject(),
    trackServiceAccessor: TrackServiceAccessor = koinInject(),
    crossinline onTrackDismissed: suspend (Int, Track) -> Boolean
) = DismissableTrackList(
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
    trackServiceAccessor = trackServiceAccessor,
    onTrackDismissed = onTrackDismissed
)