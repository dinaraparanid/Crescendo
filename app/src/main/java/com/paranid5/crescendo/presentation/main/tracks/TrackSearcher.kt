package com.paranid5.crescendo.presentation.main.tracks

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.data.tracks.Track
import com.paranid5.crescendo.presentation.ui.utils.Searcher
import kotlinx.coroutines.flow.StateFlow

@Composable
fun TrackSearcher(
    tracksState: StateFlow<List<Track>>,
    queryState: StateFlow<String?>,
    setQuery: (String?) -> Unit,
    isSearchBarActiveState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.(List<Track>, LazyListState) -> Unit)
) = Searcher(
    modifier = modifier,
    allItemsState = tracksState,
    queryState = queryState,
    setQuery = setQuery,
    filteredContent = content,
    isSearchBarActiveState = isSearchBarActiveState,
    filter = { query, track ->
        val title = track.title.lowercase()
        val artist = track.artist.lowercase()
        val album = track.album.lowercase()
        query in title || query in artist || query in album
    }
)