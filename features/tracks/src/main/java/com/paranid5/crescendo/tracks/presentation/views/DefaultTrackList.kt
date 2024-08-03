package com.paranid5.crescendo.tracks.presentation.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.system.services.track.TrackServiceInteractor
import com.paranid5.crescendo.system.services.track.startPlaylistPlayback
import com.paranid5.crescendo.tracks.presentation.TracksViewModel
import com.paranid5.crescendo.tracks.presentation.properties.compose.collectShownTracksAsState
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingPagerState
import com.paranid5.crescendo.ui.track.TrackList
import com.paranid5.crescendo.ui.track.currentTrackState
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import com.paranid5.crescendo.utils.extensions.mapToImmutableList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DefaultTrackList(
    scrollingState: LazyListState,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    bottomPadding: Dp = dimensions.padding.extraMedium,
    viewModel: TracksViewModel = koinViewModel(),
    trackServiceInteractor: TrackServiceInteractor = koinInject(),
) {
    val playingPagerState = LocalPlayingPagerState.current
    val shownTracksDTO by viewModel.collectShownTracksAsState()
    val currentTrack by currentTrackState()
    val coroutineScope = rememberCoroutineScope()

    var shownTracks by remember {
        mutableStateOf<ImmutableList<TrackUiState>>(persistentListOf())
    }

    LaunchedEffect(shownTracksDTO) {
        shownTracks = withContext(Dispatchers.Default) {
            shownTracksDTO.mapToImmutableList(TrackUiState.Companion::fromDTO)
        }
    }

    TrackList(
        tracks = shownTracks,
        scrollingState = scrollingState,
        modifier = modifier,
        bottomPadding = bottomPadding,
        trackItemContent = { trackList, trackInd, trackModifier ->
            TrackItem(
                modifier = trackModifier then trackItemModifier,
                tracks = trackList,
                trackInd = trackInd,
            ) {
                coroutineScope.launch {
                    playingPagerState?.animateScrollToPage(0)

                    viewModel.updateAudioStatus(AudioStatus.PLAYING)
                    viewModel.updateCurrentPlaylist(trackList)
                    viewModel.updateCurrentTrackIndex(trackInd)

                    trackServiceInteractor.startPlaylistPlayback(
                        nextTrack = trackList.getOrNull(trackInd),
                        prevTrack = currentTrack,
                    )
                }
            }
        }
    )
}
