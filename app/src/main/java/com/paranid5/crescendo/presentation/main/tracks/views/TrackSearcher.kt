package com.paranid5.crescendo.presentation.main.tracks.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.tracks.TracksViewModel
import com.paranid5.crescendo.presentation.main.tracks.properties.compose.collectIsSearchBarActiveAsState
import com.paranid5.crescendo.presentation.main.tracks.properties.compose.collectQueryAsState
import com.paranid5.crescendo.presentation.main.tracks.properties.compose.collectShownTracksAsState
import com.paranid5.crescendo.presentation.main.tracks.properties.compose.collectTracksAsState
import com.paranid5.crescendo.presentation.ui.utils.FilteredContent
import com.paranid5.crescendo.presentation.ui.utils.Searcher

@Composable
fun TrackSearcher(
    modifier: Modifier = Modifier,
    viewModel: TracksViewModel = koinActivityViewModel(),
    content: FilteredContent<com.paranid5.crescendo.core.common.tracks.Track>
) {
    val tracks by viewModel.collectTracksAsState()
    val shownTracks by viewModel.collectShownTracksAsState()
    val query by viewModel.collectQueryAsState()
    val isSearchBarActiveState by viewModel.collectIsSearchBarActiveAsState()

    Searcher(
        modifier = modifier,
        items = tracks,
        shownItems = shownTracks,
        setFilteredItems = viewModel::setFilteredTracks,
        query = query,
        setQuery = viewModel::setQuery,
        isSearchBarActive = isSearchBarActiveState,
        setSearchBarActive = viewModel::setSearchBarActive,
        filteredContent = content,
        filter = ::filterTrack
    )
}

private fun filterTrack(query: String, track: com.paranid5.crescendo.core.common.tracks.Track): Boolean {
    val title = track.title.lowercase()
    val artist = track.artist.lowercase()
    val album = track.album.lowercase()
    return query in title || query in artist || query in album
}