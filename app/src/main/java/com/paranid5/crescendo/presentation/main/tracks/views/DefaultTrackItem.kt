package com.paranid5.crescendo.presentation.main.tracks.views

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.currentTrackFlow
import com.paranid5.crescendo.data.states.playback.AudioStatusStatePublisher
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.presentation.main.tracks.views.item.TrackCover
import com.paranid5.crescendo.presentation.main.tracks.views.item.TrackInfo
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState
import com.paranid5.crescendo.presentation.ui.permissions.requests.audioRecordingPermissionsRequestLauncher
import com.paranid5.crescendo.presentation.ui.permissions.requests.foregroundServicePermissionsRequestLauncher
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.services.track_service.TrackServiceAccessor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun DefaultTrackItem(
    tracks: ImmutableList<Track>,
    trackInd: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val track = tracks.getOrNull(trackInd)

    if (track != null)
        Box(modifier) {
            DefaultTrackItemContent(
                track = track,
                onClick = onClick,
                permissionModifier = Modifier.align(Alignment.Center)
            )
        }
}

@Composable
fun DefaultTrackItem(
    viewModel: AudioStatusStatePublisher,
    tracks: ImmutableList<Track>,
    trackInd: Int,
    modifier: Modifier = Modifier,
    trackServiceAccessor: TrackServiceAccessor = koinInject(),
) {
    val coroutineScope = rememberCoroutineScope()

    DefaultTrackItem(
        tracks = tracks,
        trackInd = trackInd,
        modifier = modifier,
        onClick = {
            coroutineScope.launch {
                startPlaylistPlayback(
                    tracks = tracks,
                    trackInd = trackInd,
                    viewModel = viewModel,
                    trackServiceAccessor = trackServiceAccessor,
                )
            }
        }
    )
}

@Composable
private inline fun DefaultTrackItemContent(
    track: Track,
    modifier: Modifier = Modifier,
    permissionModifier: Modifier = Modifier,
    crossinline onClick: () -> Unit
) {
    val textColor by rememberTextColor(track)

    Row(
        modifier.clickableTrackWithPermissions(
            onClick = onClick,
            permissionModifier = permissionModifier
        )
    ) {
        TrackCover(
            trackPath = track.path,
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(7.dp))
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
            tint = textColor,
            iconModifier = Modifier.height(20.dp),
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
private fun rememberIsTrackCurrent(
    track: Track,
    storageHandler: StorageHandler = koinInject()
): State<Boolean> {
    val currentTrack by storageHandler
        .currentTrackFlow
        .collectLatestAsState(initial = null)

    return remember(track.path, currentTrack?.path) {
        derivedStateOf { track.path == currentTrack?.path }
    }
}

@Composable
inline fun Modifier.clickableTrackWithPermissions(
    crossinline onClick: () -> Unit,
    permissionModifier: Modifier = Modifier
): Modifier {
    val isFSPermissionDialogShownState = remember { mutableStateOf(false) }
    val isRecordingPermissionDialogShownState = remember { mutableStateOf(false) }

    val (areFSPermissionsGranted, launchFSPermissions) = foregroundServicePermissionsRequestLauncher(
        isFSPermissionDialogShownState,
        permissionModifier
    )

    val (isRecordingPermissionGranted, launchRecordPermissions) = audioRecordingPermissionsRequestLauncher(
        isRecordingPermissionDialogShownState,
        permissionModifier
    )

    return this.clickable {
        when {
            !areFSPermissionsGranted -> launchFSPermissions()
            !isRecordingPermissionGranted -> launchRecordPermissions()
            else -> onClick()
        }
    }
}