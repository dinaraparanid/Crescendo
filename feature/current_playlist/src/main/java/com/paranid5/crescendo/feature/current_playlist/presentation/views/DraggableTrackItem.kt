package com.paranid5.crescendo.feature.current_playlist.presentation.views

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.ui.track.clickableTrackWithPermissions
import com.paranid5.crescendo.ui.track.item.TrackCover
import com.paranid5.crescendo.ui.track.item.TrackInfo
import com.paranid5.crescendo.ui.track.item.TrackPropertiesButton
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import kotlinx.collections.immutable.ImmutableList

private val TrackCoverSize = 48.dp

@Composable
internal fun DraggableTrackItem(
    tracks: ImmutableList<TrackUiState>,
    trackIndex: Int,
    currentTrackDragIndex: Int,
    navigateToTrimmer: (trackUri: String) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val track by remember(tracks, trackIndex) {
        derivedStateOf { tracks.getOrNull(trackIndex) }
    }

    val isTrackCurrent by remember(trackIndex, currentTrackDragIndex) {
        derivedStateOf { trackIndex == currentTrackDragIndex }
    }

    track?.let {
        CurrentPlaylistTrackItemContent(
            track = it,
            isTrackCurrent = isTrackCurrent,
            navigateToTrimmer = navigateToTrimmer,
            modifier = modifier,
            onClick = onClick,
        )
    }
}

@Composable
private fun CurrentPlaylistTrackItemContent(
    track: TrackUiState,
    isTrackCurrent: Boolean,
    navigateToTrimmer: (trackUri: String) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val appColors = colors

    val backgroundColor by remember(isTrackCurrent, appColors) {
        derivedStateOf {
            when {
                isTrackCurrent -> appColors.selection.selected.copy(alpha = 0.25F)
                else -> Color.Transparent
            }
        }
    }

    val textColor by remember(isTrackCurrent, appColors) {
        derivedStateOf {
            if (isTrackCurrent) appColors.selection.selected else appColors.text.primary
        }
    }

    val coverShape = RoundedCornerShape(dimensions.padding.small)

    Box(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(size = dimensions.padding.extraMedium))
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
                    .clip(coverShape),
            )

            Spacer(Modifier.width(dimensions.padding.medium))

            TrackInfo(
                track = track,
                textColor = textColor,
                modifier = Modifier
                    .weight(1F)
                    .padding(start = dimensions.padding.small)
                    .align(Alignment.CenterVertically),
            )

            Spacer(Modifier.width(dimensions.padding.medium))

            TrackPropertiesButton(
                track = track,
                tint = textColor,
                navigateToTrimmer = navigateToTrimmer,
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
