package com.paranid5.crescendo.presentation.main.tracks.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.data.states.playback.AudioStatusStatePublisher
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.domain.utils.extensions.toDefaultTrackList
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.composition_locals.playing.LocalPlayingPagerState
import com.paranid5.crescendo.presentation.main.tracks.TracksViewModel
import com.paranid5.crescendo.presentation.main.tracks.properties.compose.collectShownTracksAsState
import com.paranid5.crescendo.services.track_service.TrackServiceAccessor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DefaultTrackList(
    scrollingState: LazyListState,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    viewModel: TracksViewModel = koinActivityViewModel(),
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) {
    val playingPagerState = LocalPlayingPagerState.current
    val shownTracks by viewModel.collectShownTracksAsState()
    val coroutineScope = rememberCoroutineScope()

    TrackList(
        tracks = shownTracks,
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

                    startPlaylistPlayback(
                        shownTracks,
                        trackInd,
                        viewModel,
                        trackServiceAccessor
                    )
                }
            }
        }
    )
}

internal suspend inline fun startPlaylistPlayback(
    tracks: ImmutableList<Track>,
    trackInd: Int,
    viewModel: AudioStatusStatePublisher,
    trackServiceAccessor: TrackServiceAccessor,
) {
    viewModel.setAudioStatus(AudioStatus.PLAYING)

    trackServiceAccessor.startPlaying(
        playlist = tracks.toDefaultTrackList(),
        trackInd = trackInd
    )
}