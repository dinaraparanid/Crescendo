package com.paranid5.crescendo.tracks.presentation.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistPublisher
import com.paranid5.crescendo.domain.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.tracks.CurrentTrackIndexPublisher
import com.paranid5.crescendo.system.services.track.TrackServiceInteractor
import com.paranid5.crescendo.system.services.track.startPlaylistPlayback
import com.paranid5.crescendo.ui.track.clickableTrackWithPermissions
import com.paranid5.crescendo.ui.track.currentTrackState
import com.paranid5.crescendo.ui.track.item.TrackCover
import com.paranid5.crescendo.ui.track.item.TrackInfo
import com.paranid5.crescendo.ui.track.item.TrackPropertiesButton
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private val TrackCoverSize = 64.dp

@Composable
internal fun TrackItem(
    tracks: ImmutableList<TrackUiState>,
    trackInd: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val track by remember(tracks, trackInd) {
        derivedStateOf { tracks.getOrNull(trackInd) }
    }

    track?.let {
        Box(modifier) {
            TrackItemContent(
                track = it,
                onClick = onClick,
                permissionModifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
internal fun <VM> TrackItem(
    viewModel: VM,
    tracks: ImmutableList<TrackUiState>,
    trackInd: Int,
    modifier: Modifier = Modifier,
    trackServiceInteractor: TrackServiceInteractor = koinInject(),
) where VM : AudioStatusPublisher,
        VM : CurrentPlaylistPublisher,
        VM : CurrentTrackIndexPublisher {
    val coroutineScope = rememberCoroutineScope()
    val currentTrack by currentTrackState()

    TrackItem(
        tracks = tracks,
        trackInd = trackInd,
        modifier = modifier,
        onClick = {
            coroutineScope.launch {
                viewModel.updateAudioStatus(AudioStatus.PLAYING)
                viewModel.updateCurrentPlaylist(tracks)
                viewModel.updateCurrentTrackIndex(trackInd)

                trackServiceInteractor.startPlaylistPlayback(
                    nextTrack = tracks.getOrNull(trackInd),
                    prevTrack = currentTrack,
                )
            }
        }
    )
}

@Composable
private fun TrackItemContent(
    track: TrackUiState,
    modifier: Modifier = Modifier,
    permissionModifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isTrackCurrent by rememberIsTrackCurrent(track = track)
    val textColor by rememberTrackTextColor(isTrackCurrent = isTrackCurrent)

    Row(
        modifier
            .clip(RoundedCornerShape(size = dimensions.padding.extraMedium))
            .clickableTrackWithPermissions(
                onClick = onClick,
                permissionModifier = permissionModifier,
            ),
    ) {
        TrackCover(
            trackPath = track.path,
            modifier = Modifier
                .padding(
                    top = dimensions.padding.small,
                    bottom = dimensions.padding.small,
                    start = dimensions.padding.small,
                )
                .size(TrackCoverSize)
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(dimensions.padding.small)),
        )

        Spacer(Modifier.width(dimensions.padding.small))

        TrackInfo(
            track = track,
            textColor = textColor,
            modifier = Modifier
                .weight(1F)
                .padding(start = dimensions.padding.small)
                .align(Alignment.CenterVertically),
        )

        Spacer(Modifier.width(dimensions.padding.small))

        TrackPropertiesButton(
            track = track,
            tint = textColor,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}

@Composable
fun rememberTrackBackgroundColor(isTrackCurrent: Boolean): State<Color> {
    val appColors = colors

    return remember(isTrackCurrent, appColors) {
        derivedStateOf {
            when {
                isTrackCurrent -> appColors.selection.selected.copy(alpha = 0.25F)
                else -> Color.Transparent
            }
        }
    }
}

@Composable
fun rememberTrackTextColor(isTrackCurrent: Boolean): State<Color> {
    val appColors = colors

    return remember(isTrackCurrent, appColors) {
        derivedStateOf {
            if (isTrackCurrent) appColors.selection.selected else appColors.text.primary
        }
    }
}

@Composable
private fun rememberIsTrackCurrent(track: Track): State<Boolean> {
    val currentTrack by currentTrackState()

    return remember(track.path, currentTrack?.path) {
        derivedStateOf { track.path == currentTrack?.path }
    }
}
