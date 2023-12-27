package com.paranid5.crescendo.presentation.main.tracks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.presentation.composition_locals.playing.LocalPlayingPagerState
import com.paranid5.crescendo.services.track_service.TrackServiceAccessor
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

typealias TrackItemView = @Composable (
    tracks: List<Track>,
    trackInd: Int,
    modifier: Modifier
) -> Unit

@Composable
fun TrackList(
    tracks: List<Track>,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackList(
    tracks: List<Track>,
    scrollingState: LazyListState,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject(),
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) {
    val playingPagerState = LocalPlayingPagerState.current
    val coroutineScope = rememberCoroutineScope()

    TrackList(
        tracks = tracks,
        scrollingState = scrollingState,
        modifier = modifier,
        trackItemView = { trackList, trackInd, trackModifier ->
            DefaultTrackItem(
                modifier = trackModifier then trackItemModifier,
                tracks = trackList,
                trackInd = trackInd
            ) {
                coroutineScope.launch {
                    playingPagerState?.animateScrollToPage(0)
                    startPlaylistPlayback(tracks, trackInd, storageHandler, trackServiceAccessor)
                }
            }
        }
    )
}