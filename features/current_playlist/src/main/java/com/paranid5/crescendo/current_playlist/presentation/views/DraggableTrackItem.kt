package com.paranid5.crescendo.current_playlist.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.current_playlist.presentation.CurrentPlaylistViewModel
import com.paranid5.crescendo.domain.interactor.tracks.startPlaylistPlayback
import com.paranid5.crescendo.system.services.track.TrackServiceAccessor
import com.paranid5.crescendo.ui.track.clickableTrackWithPermissions
import com.paranid5.crescendo.ui.track.currentTrackState
import com.paranid5.crescendo.ui.track.item.TrackCover
import com.paranid5.crescendo.ui.track.item.TrackInfo
import com.paranid5.crescendo.ui.track.item.TrackPropertiesButton
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal inline fun <T : Track> DraggableTrackItem(
    tracks: ImmutableList<T>,
    trackIndex: Int,
    currentTrackDragIndex: Int,
    modifier: Modifier = Modifier,
    crossinline onClick: () -> Unit
) {
    val colors = LocalAppColors.current

    val track = tracks.getOrNull(trackIndex)

    val isTrackCurrent by remember(trackIndex, currentTrackDragIndex) {
        derivedStateOf { trackIndex == currentTrackDragIndex }
    }

    val textColor by remember(isTrackCurrent) {
        derivedStateOf { if (isTrackCurrent) colors.primary else colors.fontColor }
    }

    if (track != null)
        CurrentPlaylistTrackItemContent(
            track = track,
            textColor = textColor,
            onClick = onClick,
            modifier = modifier
        )
}

@Composable
internal fun <T : Track> DraggableTrackItem(
    tracks: ImmutableList<T>,
    trackIndex: Int,
    currentTrackDragIndex: Int,
    modifier: Modifier = Modifier,
    viewModel: CurrentPlaylistViewModel = koinViewModel(),
    trackServiceAccessor: TrackServiceAccessor = koinInject(),
) {
    val coroutineScope = rememberCoroutineScope()
    val currentTrack by currentTrackState()

    DraggableTrackItem(
        tracks = tracks,
        trackIndex = trackIndex,
        currentTrackDragIndex = currentTrackDragIndex,
        modifier = modifier,
        onClick = {
            coroutineScope.launch {
                trackServiceAccessor.startPlaylistPlayback(
                    newTracks = tracks,
                    newTrackIndex = trackIndex,
                    currentTrack = currentTrack,
                    source = viewModel,
                )
            }
        }
    )
}

@Composable
private inline fun <T : Track> CurrentPlaylistTrackItemContent(
    track: T,
    textColor: Color,
    crossinline onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    Box(modifier) {
        Row(
            modifier
                .clip(RoundedCornerShape(size = 16.dp))
                .background(brush = colors.itemBackgroundGradient)
                .clickableTrackWithPermissions(
                    onClick = onClick,
                    permissionModifier = Modifier.align(Alignment.Center)
                )
        ) {
            CurrentPlaylistTrackCover(
                track = track,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp, start = 8.dp)
                    .size(64.dp)
                    .align(Alignment.CenterVertically)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(Modifier.width(8.dp))

            TrackInfo(
                track = track,
                textColor = textColor,
                modifier = Modifier
                    .weight(1F)
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically)
            )

            Spacer(Modifier.width(8.dp))

            TrackPropertiesButton(
                track = track,
                tint = colors.fontColor,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }
    }
}

@Composable
private fun <T : Track> CurrentPlaylistTrackCover(
    track: T,
    modifier: Modifier = Modifier
) = TrackCover(
    trackPath = track.path,
    modifier = modifier
)