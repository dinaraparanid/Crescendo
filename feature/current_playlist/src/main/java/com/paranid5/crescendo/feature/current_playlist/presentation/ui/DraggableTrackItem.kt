package com.paranid5.crescendo.feature.current_playlist.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.ui.track.clickableTrackWithPermissions
import com.paranid5.crescendo.ui.track.item.TrackCover
import com.paranid5.crescendo.ui.track.item.TrackInfo
import com.paranid5.crescendo.ui.track.item.TrackKebabMenuButton
import com.paranid5.crescendo.ui.track.item.rememberTrackBackgroundColors
import com.paranid5.crescendo.ui.track.item.rememberTrackContentColor
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import kotlinx.collections.immutable.ImmutableList

private val TrackCoverSize = 48.dp

@Composable
internal fun DraggableTrackItem(
    tracks: ImmutableList<TrackUiState>,
    trackIndex: Int,
    isCurrent: Boolean,
    addToPlaylist: (track: Track) -> Unit,
    showTrimmer: (trackUri: String) -> Unit,
    showMetaEditor: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val track by remember(tracks, trackIndex) {
        derivedStateOf { tracks.getOrNull(trackIndex) }
    }

    track?.let {
        CurrentPlaylistTrackItemContent(
            track = it,
            isCurrent = isCurrent,
            addToPlaylist = addToPlaylist,
            showTrimmer = showTrimmer,
            showMetaEditor = showMetaEditor,
            modifier = modifier,
            onClick = onClick,
        )
    }
}

@Composable
private fun CurrentPlaylistTrackItemContent(
    track: TrackUiState,
    isCurrent: Boolean,
    addToPlaylist: (track: Track) -> Unit,
    showTrimmer: (trackUri: String) -> Unit,
    showMetaEditor: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val backgroundColor by rememberTrackBackgroundColors(isCurrent = isCurrent)
    val contentColor by rememberTrackContentColor(isCurrent = isCurrent)

    Box(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(dimensions.padding.extraMedium))
                .background(backgroundColor)
                .clickableTrackWithPermissions(
                    permissionModifier = Modifier.align(Alignment.Center),
                    onClick = onClick,
                )
        ) {
            Spacer(Modifier.width(dimensions.padding.small))

            CurrentPlaylistTrackCover(
                trackPath = track.path,
                modifier = Modifier
                    .padding(
                        top = dimensions.padding.medium,
                        bottom = dimensions.padding.medium,
                    )
                    .size(TrackCoverSize)
                    .align(Alignment.CenterVertically)
                    .clip(RoundedCornerShape(dimensions.padding.small)),
            )

            Spacer(Modifier.width(dimensions.padding.medium))

            TrackInfo(
                track = track,
                textColor = contentColor,
                modifier = Modifier
                    .weight(1F)
                    .padding(start = dimensions.padding.small)
                    .align(Alignment.CenterVertically),
            )

            Spacer(Modifier.width(dimensions.padding.medium))

            TrackKebabMenuButton(
                track = track,
                tint = contentColor,
                addToPlaylist = addToPlaylist,
                showTrimmer = showTrimmer,
                showMetaEditor = showMetaEditor,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }
    }
}

@Composable
private fun CurrentPlaylistTrackCover(
    trackPath: String,
    modifier: Modifier = Modifier
) = TrackCover(
    trackPath = trackPath,
    modifier = modifier,
)
