@file:Suppress("LongLine")

package com.paranid5.crescendo.presentation.main.current_playlist

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
import androidx.compose.runtime.IntState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.presentation.main.tracks.TrackCover
import com.paranid5.crescendo.presentation.main.tracks.TrackInfo
import com.paranid5.crescendo.presentation.main.tracks.TrackPropertiesButton
import com.paranid5.crescendo.presentation.main.tracks.startPlaylistPlayback
import com.paranid5.crescendo.presentation.ui.permissions.requests.audioRecordingPermissionsRequestLauncher
import com.paranid5.crescendo.presentation.ui.permissions.requests.foregroundServicePermissionsRequestLauncher
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.services.track_service.TrackServiceAccessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CurrentPlaylistTrackItem(
    tracks: List<Track>,
    trackInd: Int,
    currentTrackDragIndState: IntState,
    scope: CoroutineScope,
    storageHandler: StorageHandler,
    trackServiceAccessor: TrackServiceAccessor,
    modifier: Modifier = Modifier,
    onClick: suspend () -> Unit = suspend {
        startPlaylistPlayback(tracks, trackInd, storageHandler, trackServiceAccessor)
    }
) {
    val colors = LocalAppColors.current.value
    val currentTrackDragInd by currentTrackDragIndState

    val trackMb by remember { derivedStateOf { tracks.getOrNull(trackInd) } }
    val trackPath by remember { derivedStateOf { trackMb?.path } }

    val isTrackCurrent by remember {
        derivedStateOf { trackInd == currentTrackDragInd }
    }

    val textColor by remember {
        derivedStateOf { if (isTrackCurrent) colors.primary else colors.inverseSurface }
    }

    val isForegroundServicePermissionDialogShownState = remember { mutableStateOf(false) }
    val isAudioRecordingPermissionDialogShownState = remember { mutableStateOf(false) }

    trackMb?.let { track ->
        Box(modifier) {
            val (areForegroundPermissionsGranted, launchFSPermissions) = foregroundServicePermissionsRequestLauncher(
                isForegroundServicePermissionDialogShownState,
                modifier = Modifier.align(Alignment.Center)
            )

            val (isRecordingPermissionGranted, launchRecordPermissions) = audioRecordingPermissionsRequestLauncher(
                isAudioRecordingPermissionDialogShownState,
                modifier = Modifier.align(Alignment.Center)
            )

            Row(
                modifier.clickable {
                    if (!areForegroundPermissionsGranted) {
                        launchFSPermissions()
                        return@clickable
                    }

                    if (!isRecordingPermissionGranted) {
                        launchRecordPermissions()
                        return@clickable
                    }

                    scope.launch { onClick() }
                }
            ) {
                TrackCover(
                    trackPath = trackPath ?: "",
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
                    iconModifier = Modifier.height(20.dp),
                    storageHandler = storageHandler,
                    trackServiceAccessor = trackServiceAccessor
                )
            }
        }
    }
}