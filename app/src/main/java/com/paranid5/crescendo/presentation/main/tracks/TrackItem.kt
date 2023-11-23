package com.paranid5.crescendo.presentation.main.tracks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.paranid5.crescendo.R
import com.paranid5.crescendo.data.tracks.Track
import com.paranid5.crescendo.data.utils.extensions.artistAlbum
import com.paranid5.crescendo.data.utils.extensions.toDefaultTrackList
import com.paranid5.crescendo.domain.StorageHandler
import com.paranid5.crescendo.domain.services.track_service.TrackServiceAccessor
import com.paranid5.crescendo.presentation.main.AudioStatus
import com.paranid5.crescendo.presentation.main.getTrackCoverModel
import com.paranid5.crescendo.presentation.ui.permissions.requests.audioRecordingPermissionsRequestLauncher
import com.paranid5.crescendo.presentation.ui.permissions.requests.foregroundServicePermissionsRequestLauncher
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DefaultTrackItem(
    tracks: List<Track>,
    trackInd: Int,
    scope: CoroutineScope,
    storageHandler: StorageHandler,
    trackServiceAccessor: TrackServiceAccessor,
    modifier: Modifier = Modifier,
    onClick: suspend () -> Unit = suspend {
        startPlaylistPlayback(tracks, trackInd, storageHandler, trackServiceAccessor)
    }
) {
    val colors = LocalAppColors.current.value
    val currentTrack by storageHandler.currentTrackState.collectAsState()

    val trackMb by remember { derivedStateOf { tracks.getOrNull(trackInd) } }
    val trackPath by remember { derivedStateOf { trackMb?.path } }

    val isTrackCurrent by remember {
        derivedStateOf { trackPath == currentTrack?.path }
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

internal suspend inline fun startPlaylistPlayback(
    tracks: List<Track>,
    trackInd: Int,
    storageHandler: StorageHandler,
    trackServiceAccessor: TrackServiceAccessor
) {
    storageHandler.storeAudioStatus(AudioStatus.PLAYING)

    trackServiceAccessor.startPlaying(
        playlist = tracks.toDefaultTrackList(),
        trackInd = trackInd
    )
}

@Composable
fun TrackCover(trackPath: String, modifier: Modifier = Modifier) {
    val trackCover = getTrackCoverModel(
        path = trackPath,
        isPlaceholderRequired = true,
        size = 200 to 200,
        animationMillis = 250
    )

    AsyncImage(
        model = trackCover,
        contentDescription = stringResource(id = R.string.track_cover),
        alignment = Alignment.Center,
        contentScale = ContentScale.FillBounds,
        modifier = modifier
    )
}

@Composable
fun TrackInfo(
    track: Track,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        TrackTitle(
            modifier = Modifier.align(Alignment.Start),
            trackTitle = track.title,
            textColor = textColor,
        )

        TrackArtistAlbum(
            modifier = Modifier.align(Alignment.Start),
            trackArtistAlbum = track.artistAlbum,
            textColor = textColor,
        )
    }
}

@Composable
private fun TrackTitle(
    trackTitle: String,
    textColor: Color,
    modifier: Modifier = Modifier
) = TrackText(
    modifier = modifier,
    text = trackTitle,
    textColor = textColor,
    fontSize = 18.sp,
)

@Composable
private fun TrackArtistAlbum(
    trackArtistAlbum: String,
    textColor: Color,
    modifier: Modifier = Modifier
) = TrackText(
    modifier = modifier,
    text = trackArtistAlbum,
    textColor = textColor,
    fontSize = 15.sp,
)

@Composable
private fun TrackText(
    text: String,
    textColor: Color,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) = Text(
    modifier = modifier,
    text = text,
    color = textColor,
    fontSize = fontSize,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)