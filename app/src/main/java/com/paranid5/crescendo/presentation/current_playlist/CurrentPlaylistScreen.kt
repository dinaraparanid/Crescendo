package com.paranid5.crescendo.presentation.current_playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.data.utils.extensions.timeString
import com.paranid5.crescendo.data.utils.extensions.totalDuration
import com.paranid5.crescendo.domain.StorageHandler
import com.paranid5.crescendo.domain.services.track_service.TrackServiceAccessor
import com.paranid5.crescendo.presentation.tracks.DismissableTrackList
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.theme.TransparentUtility
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(TransparentUtility)
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

    Row(modifier.fillMaxWidth()) {
        Text(
            text = "${stringResource(R.string.tracks)}: $tracksNumber",
            color = colors.primary,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1F)
        )

        Text(
            text = "${stringResource(R.string.duration)}: ${totalDuration.timeString}",
            color = colors.primary,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1F)
        )
    }
}