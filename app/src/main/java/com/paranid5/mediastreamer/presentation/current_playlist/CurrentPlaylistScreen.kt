package com.paranid5.mediastreamer.presentation.current_playlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.data.utils.extensions.timeString
import com.paranid5.mediastreamer.data.utils.extensions.totalDuration
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.domain.services.track_service.TrackServiceAccessor
import com.paranid5.mediastreamer.presentation.tracks.DismissableTrackList
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import org.koin.compose.koinInject

@Composable
fun CurrentPlaylistScreen(
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject(),
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) {
    val currentPlaylistMb by storageHandler.currentPlaylistState.collectAsState()
    val currentTrackIndex by storageHandler.currentTrackIndexState.collectAsState()
    val currentPlaylist by remember { derivedStateOf { currentPlaylistMb ?: listOf() } }
    val scrollingState = rememberLazyListState()

    Column(modifier) {
        CurrentPlaylistBar(
            tracksNumber = currentPlaylist.size,
            totalDuration = currentPlaylist.totalDuration,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)
        )

        DismissableTrackList(
            tracks = currentPlaylist,
            scrollingState = scrollingState,
            modifier = modifier.padding(start = 10.dp, end = 5.dp, bottom = 10.dp),
            storageHandler = storageHandler
        ) { index, _ ->
            if (index != currentTrackIndex) {
                val newPlaylist = currentPlaylist.take(index) + currentPlaylist.drop(index + 1)
                storageHandler.storeCurrentPlaylist(newPlaylist)

                if (index < currentTrackIndex)
                    storageHandler.storeCurrentTrackIndex(currentTrackIndex - 1)

                trackServiceAccessor.removeFromPlaylist(index)
            }
        }
    }
}

@Composable
private fun CurrentPlaylistBar(
    modifier: Modifier = Modifier,
    tracksNumber: Int,
    totalDuration: Long
) {
    val colors = LocalAppColors.current.value

    Text(
        text = "${stringResource(R.string.tracks)}: $tracksNumber ${stringResource(R.string.duration)}: ${totalDuration.timeString}",
        color = colors.primary,
        textAlign = TextAlign.Start,
        modifier = modifier
    )
}