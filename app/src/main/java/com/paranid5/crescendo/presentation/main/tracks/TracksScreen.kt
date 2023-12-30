package com.paranid5.crescendo.presentation.main.tracks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.presentation.main.tracks.effects.LoadTracksFromMediaStoreEffect
import com.paranid5.crescendo.presentation.main.tracks.properties.compose.collectSearchBarHeightDpAsState
import com.paranid5.crescendo.presentation.main.tracks.properties.setFilteredTracks
import com.paranid5.crescendo.presentation.main.tracks.views.DefaultTrackList
import com.paranid5.crescendo.presentation.main.tracks.views.TrackSearcher
import com.paranid5.crescendo.presentation.main.tracks.views.TracksBar

@Composable
fun TracksScreen(
    viewModel: TracksViewModel,
    modifier: Modifier = Modifier,
) {
    val searchBarHeight by viewModel.collectSearchBarHeightDpAsState()
    var tracksScrollingState = rememberLazyListState()

    LoadTracksFromMediaStoreEffect()

    Column(modifier) {
        TrackSearcher(
            Modifier
                .fillMaxWidth()
                .height(searchBarHeight)
        ) { filtered, scrollingState ->
            viewModel.setFilteredTracks(filtered)
            tracksScrollingState = scrollingState
        }

        TracksBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
        )

        Spacer(Modifier.height(10.dp))

        DefaultTrackList(
            scrollingState = tracksScrollingState,
            modifier = Modifier
                .fillMaxSize(1F)
                .padding(horizontal = 5.dp)
        )
    }
}
