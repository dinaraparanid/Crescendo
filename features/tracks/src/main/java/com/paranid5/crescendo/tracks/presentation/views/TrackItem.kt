package com.paranid5.crescendo.tracks.presentation.views

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
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.domain.interactor.tracks.startPlaylistPlayback
import com.paranid5.crescendo.domain.sources.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentPlaylistPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackIndexPublisher
import com.paranid5.crescendo.system.services.track.TrackServiceAccessor
import com.paranid5.crescendo.ui.track.clickableTrackWithPermissions
import com.paranid5.crescendo.ui.track.currentTrackState
import com.paranid5.crescendo.ui.track.item.TrackCover
import com.paranid5.crescendo.ui.track.item.TrackInfo
import com.paranid5.crescendo.ui.track.item.TrackPropertiesButton
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun TrackItem(
    tracks: ImmutableList<Track>,
    trackInd: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val track = tracks.getOrNull(trackInd)

    if (track != null)
        Box(modifier) {
            TrackItemContent(
                track = track,
                onClick = onClick,
                permissionModifier = Modifier.align(Alignment.Center)
            )
        }
}

@Composable
internal fun <VM> TrackItem(
    viewModel: VM,
    tracks: ImmutableList<Track>,
    trackInd: Int,
    modifier: Modifier = Modifier,
    trackServiceAccessor: TrackServiceAccessor = koinInject(),
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
                trackServiceAccessor.startPlaylistPlayback(
                    newTracks = tracks,
                    newTrackIndex = trackInd,
                    currentTrack = currentTrack,
                    source = viewModel,
                )
            }
        }
    )
}

@Composable
private inline fun TrackItemContent(
    track: Track,
    modifier: Modifier = Modifier,
    permissionModifier: Modifier = Modifier,
    crossinline onClick: () -> Unit
) {
    val colors = LocalAppColors.current
    val textColor by rememberTextColor(track)

    Row(
        modifier
            .clip(RoundedCornerShape(size = 16.dp))
            .background(brush = colors.itemBackgroundGradient)
            .clickableTrackWithPermissions(
                onClick = onClick,
                permissionModifier = permissionModifier
            )
    ) {
        TrackCover(
            trackPath = track.path,
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
            tint = textColor,
            modifier = Modifier.align(Alignment.CenterVertically),
            iconModifier = Modifier.height(18.dp),
        )
    }
}

@Composable
private fun rememberTextColor(track: Track): State<Color> {
    val colors = LocalAppColors.current
    val isTrackCurrent by rememberIsTrackCurrent(track)

    return remember {
        derivedStateOf {
            if (isTrackCurrent) colors.primary else colors.fontColor
        }
    }
}

@Composable
private fun rememberIsTrackCurrent(track: Track): State<Boolean> {
    val currentTrack by currentTrackState()

    return remember(track.path, currentTrack?.path) {
        derivedStateOf {
            track.path == currentTrack?.path
        }
    }
}