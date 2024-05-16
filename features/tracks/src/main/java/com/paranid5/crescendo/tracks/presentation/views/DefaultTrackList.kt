package com.paranid5.crescendo.tracks.presentation.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.domain.interactor.tracks.startPlaylistPlayback
import com.paranid5.crescendo.system.services.track.TrackServiceAccessor
import com.paranid5.crescendo.tracks.presentation.TracksViewModel
import com.paranid5.crescendo.tracks.presentation.properties.compose.collectShownTracksAsState
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingPagerState
import com.paranid5.crescendo.ui.track.TrackList
import com.paranid5.crescendo.ui.track.currentTrackState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DefaultTrackList(
    scrollingState: LazyListState,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    bottomPadding: Dp = 16.dp,
    viewModel: TracksViewModel = koinViewModel(),
    trackServiceAccessor: TrackServiceAccessor = koinInject(),
) {
    val playingPagerState = LocalPlayingPagerState.current
    val shownTracks by viewModel.collectShownTracksAsState()
    val currentTrack by currentTrackState()
    val coroutineScope = rememberCoroutineScope()

    TrackList(
        tracks = shownTracks,
        scrollingState = scrollingState,
        modifier = modifier,
        bottomPadding = bottomPadding,
        trackItemView = { trackList, trackInd, trackModifier ->
            TrackItem(
                modifier = trackModifier then trackItemModifier,
                tracks = trackList,
                trackInd = trackInd
            ) {
                coroutineScope.launch {
                    playingPagerState?.animateScrollToPage(0)

                    trackServiceAccessor.startPlaylistPlayback(
                        newTracks = shownTracks,
                        newTrackIndex = trackInd,
                        currentTrack = currentTrack,
                        source = viewModel,
                    )
                }
            }
        }
    )
}