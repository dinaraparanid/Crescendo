package com.paranid5.crescendo.ui.track.item.properties

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState

@Composable
internal fun AddToCurrentPlaylistProperty(
    track: TrackUiState,
    modifier: Modifier = Modifier,
    addToPlaylist: (track: Track) -> Unit,
) = DropdownMenuItem(
    modifier = modifier,
    leadingIcon = { PropertyIcon(ImageVector.vectorResource(R.drawable.ic_playlist)) },
    text = { PropertyText(stringResource(R.string.track_kebab_add_to_cur_playlist)) },
    onClick = { addToPlaylist(track) },
)
