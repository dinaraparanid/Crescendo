package com.paranid5.mediastreamer.presentation.ui.utils

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.data.tracks.Track
import com.paranid5.mediastreamer.data.utils.extensions.replaceWith
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> Searcher(
    allItemsState: StateFlow<List<T>>,
    queryState: MutableStateFlow<String?>,
    modifier: Modifier = Modifier,
    filter: (query: String, item: T) -> Boolean,
    filteredContent: @Composable (ColumnScope.(List<T>, LazyListState) -> Unit)
) {
    val queryText by queryState.collectAsState()
    val allItems by allItemsState.collectAsState()

    val shownItems = remember { mutableStateListOf<T>() }
    val scrollListScope = rememberCoroutineScope()
    val trackListState = rememberLazyListState()

    LaunchedEffect(key1 = allItems, key2 = queryText) {
        onFilterUpdate(allItems, shownItems, queryText, filter)
    }

    SearchBar(
        modifier = modifier,
        query = queryText ?: "",
        leadingIcon = { SearchIcon(Modifier.size(30.dp)) },
        placeholder = { Text(stringResource(R.string.track_input_filter_hint)) },
        colors = searcherColors,
        onQueryChange = { query ->
            queryState.update { query }
            scrollListScope.launch { trackListState.scrollToItem(0) }
        },
        onSearch = {},
        active = true,
        onActiveChange = {},
    ) {
        Spacer(Modifier.height(10.dp))
        filteredContent(shownItems, trackListState)
    }
}

@Composable
private fun SearchIcon(modifier: Modifier = Modifier) {
    val primaryColor = LocalAppColors.current.value.primary

    Icon(
        painter = painterResource(R.drawable.search_icon),
        contentDescription = null,
        tint = primaryColor,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
private inline val searcherColors
    @Composable
    get() = SearchBarDefaults.colors(
        containerColor = Color.Transparent,
        inputFieldColors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.Gray
        )
    )

private fun <T> onFilterUpdate(
    allItems: List<T>,
    shownItems: MutableList<T>,
    queryText: String?,
    filter: (query: String, item: T) -> Boolean
) = shownItems.replaceWith(
    when {
        queryText.isNullOrEmpty() -> allItems

        else -> {
            val query = queryText.lowercase()
            allItems.filter { track -> filter(query, track) }
        }
    }
)