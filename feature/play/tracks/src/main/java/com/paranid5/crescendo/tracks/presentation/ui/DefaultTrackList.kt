package com.paranid5.crescendo.tracks.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.tracks.view_model.TracksState
import com.paranid5.crescendo.tracks.view_model.TracksUiIntent
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingPagerState
import com.paranid5.crescendo.ui.composition_locals.playing.PlayingPage
import com.paranid5.crescendo.ui.foundation.getOrDefault
import com.paranid5.crescendo.ui.track.TrackList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DefaultTrackList(
    state: TracksState,
    onUiIntent: (TracksUiIntent) -> Unit,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    bottomPadding: Dp = dimensions.padding.extraMedium,
) {
    val playingPagerState = LocalPlayingPagerState.current
    val coroutineScope = rememberCoroutineScope()

    TrackList(
        tracks = state.shownTracksState.getOrDefault(persistentListOf()),
        modifier = modifier,
        bottomPadding = bottomPadding,
        trackItemContent = { trackList, trackInd, trackModifier ->
            val track by remember(trackList, trackInd) {
                derivedStateOf { trackList.getOrNull(trackInd) }
            }

            val isCurrent by remember(state, track) {
                derivedStateOf { state.currentTrack == track }
            }

            track?.let {
                TrackItem(
                    track = it,
                    isCurrent = isCurrent,
                    onUiIntent = onUiIntent,
                    modifier = trackModifier then trackItemModifier,
                ) {
                    coroutineScope.launch {
                        playingPagerState?.animateScrollToPage(PlayingPage.TRACK.ordinal)

                        onUiIntent(
                            TracksUiIntent.Tracks.TrackClick(
                                nextPlaylist = trackList,
                                nextTrackIndex = trackInd,
                            )
                        )
                    }
                }
            }
        }
    )
}
