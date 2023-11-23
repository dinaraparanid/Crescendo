package com.paranid5.crescendo.presentation.main.tracks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.data.tracks.Track
import com.paranid5.crescendo.data.tracks.sortedBy
import com.paranid5.crescendo.domain.StorageHandler
import org.koin.compose.koinInject

@Composable
fun TracksScreen(
    tracksViewModel: TracksViewModel,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject()
) {
    val allTracks by tracksViewModel.tracksState.collectAsState()
    val filterTracksState = remember { mutableStateOf(listOf<Track>()) }
    var showsTracks by remember { mutableStateOf(listOf<Track>()) }

    var tracksScrollingState = rememberLazyListState()
    val isSearchBarActiveState = remember { mutableStateOf(false) }
    val trackOrder by storageHandler.trackOrderState.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        tracksViewModel.setTracks(context.allTracksFromMediaStore)
    }

    LaunchedEffect(key1 = allTracks, key2 = filterTracksState.value, key3 = trackOrder) {
        showsTracks = when (isSearchBarActiveState.value) {
            false -> allTracks.sortedBy(trackOrder)
            true -> filterTracksState.value.sortedBy(trackOrder)
        }
    }

    Column(modifier) {
        TrackSearcher(
            tracksState = tracksViewModel.tracksState,
            queryState = tracksViewModel.queryState,
            setQuery = tracksViewModel::setQueryState,
            isSearchBarActiveState = isSearchBarActiveState,
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    when (isSearchBarActiveState.value) {
                        true -> 80.dp
                        false -> 60.dp
                    }
                )
        ) { filteredTracks, scrollingState ->
            filterTracksState.value = filteredTracks
            tracksScrollingState = scrollingState
        }

        TracksNumberOrderBar(
            tracksNumber = showsTracks.size,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
        )

        Spacer(Modifier.height(10.dp))

        TrackList(
            tracks = showsTracks,
            scrollingState = tracksScrollingState,
            modifier = Modifier
                .fillMaxSize(1F)
                .padding(horizontal = 5.dp)
        )
    }
}