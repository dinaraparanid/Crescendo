package com.paranid5.crescendo.presentation.main.current_playlist.views

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
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.current_playlist.CurrentPlaylistViewModel
import com.paranid5.crescendo.presentation.main.tracks.views.TrackPropertiesButton
import com.paranid5.crescendo.presentation.main.tracks.views.clickableTrackWithPermissions
import com.paranid5.crescendo.presentation.main.tracks.views.item.TrackCover
import com.paranid5.crescendo.presentation.main.tracks.views.item.TrackInfo
import com.paranid5.crescendo.presentation.main.tracks.views.startPlaylistPlayback
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.services.track_service.TrackServiceAccessor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
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
fun <T : Track> DraggableTrackItem(
    tracks: ImmutableList<T>,
    trackIndex: Int,
    currentTrackDragIndex: Int,
    modifier: Modifier = Modifier,
    viewModel: CurrentPlaylistViewModel = koinActivityViewModel(),
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()

    DraggableTrackItem(
        tracks = tracks,
        trackIndex = trackIndex,
        currentTrackDragIndex = currentTrackDragIndex,
        modifier = modifier,
        onClick = {
            coroutineScope.launch {
                startPlaylistPlayback(
                    tracks,
                    trackIndex,
                    viewModel,
                    trackServiceAccessor
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
            modifier.clickableTrackWithPermissions(
                onClick = onClick,
                permissionModifier = Modifier.align(Alignment.Center)
            )
        ) {
            CurrentPlaylistTrackCover(
                track = track,
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.CenterVertically)
            )

            Spacer(Modifier.width(5.dp))

            TrackInfo(
                track = track,
                textColor = textColor,
                modifier = Modifier
                    .weight(1F)
                    .padding(start = 5.dp)
                    .align(Alignment.CenterVertically)
            )

            Spacer(Modifier.width(5.dp))

            TrackPropertiesButton(
                track = track,
                tint = colors.fontColor,
                modifier = Modifier.align(Alignment.CenterVertically),
                iconModifier = Modifier.height(20.dp)
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
    modifier = modifier.clip(RoundedCornerShape(7.dp))
)