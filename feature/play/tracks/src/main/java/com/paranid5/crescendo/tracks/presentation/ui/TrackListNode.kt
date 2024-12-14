package com.paranid5.crescendo.tracks.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.tracks.view_model.TracksState
import com.paranid5.crescendo.tracks.view_model.TracksUiIntent
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingPagerState
import com.paranid5.crescendo.ui.composition_locals.playing.PlayingPage
import com.paranid5.crescendo.ui.foundation.getOrNull
import com.paranid5.crescendo.ui.track.AppTrackList
import com.paranid5.crescendo.utils.extensions.orNil
import kotlinx.coroutines.launch

@Composable
internal fun TrackListNode(
    state: TracksState,
    onUiIntent: (TracksUiIntent) -> Unit,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val playingPagerState = LocalPlayingPagerState.current
    val coroutineScope = rememberCoroutineScope()

    val tracks by remember(state.shownTracksState) {
        derivedStateOf { state.shownTracksState.getOrNull().orNil() }
    }

    AppTrackList(
        tracks = tracks,
        key = { _, track -> track.path },
        modifier = modifier,
        contentPadding = contentPadding,
        itemContent = { trackList, trackInd, trackModifier ->
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
