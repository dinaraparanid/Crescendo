package com.paranid5.crescendo.presentation.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> Searcher(
    allItemsState: StateFlow<List<T>>,
    queryState: StateFlow<String?>,
    setQuery: (String?) -> Unit,
    modifier: Modifier = Modifier,
    isSearchBarActiveState: MutableState<Boolean> = remember { mutableStateOf(false) },
    filter: (query: String, item: T) -> Boolean,
    filteredContent: @Composable (ColumnScope.(List<T>, LazyListState) -> Unit)
) {
    val colors = LocalAppColors.current.colorScheme

    val queryText by queryState.collectAsState()
    val allItems by allItemsState.collectAsState()
    val shownItems = remember { mutableStateOf(listOf<T>()) }

    val scrollListScope = rememberCoroutineScope()
    val trackListState = rememberLazyListState()

    LaunchedEffect(key1 = allItems, key2 = queryText) {
        onFilterUpdate(allItems, shownItems, queryText, filter)
    }

    SearchBar(
        modifier = modifier,
        query = queryText ?: "",
        leadingIcon = { SearchIcon(Modifier.size(30.dp)) },
        trailingIcon = {
            if (isSearchBarActiveState.value)
                CancelSearchIcon(
                    queryState = queryState,
                    setQuery = setQuery,
                    isSearchBarActiveState = isSearchBarActiveState,
                    modifier = Modifier.size(20.dp)
                )
        },
        placeholder = {
            Text(
                text = stringResource(R.string.track_input_filter_hint),
                color = colors.inverseSurface
            )
        },
        colors = searcherColors,
        windowInsets = WindowInsets(0.dp),
        onQueryChange = { query ->
            setQuery(query)
            scrollListScope.launch { trackListState.scrollToItem(0) }
        },
        active = isSearchBarActiveState.value,
        onSearch = { isSearchBarActiveState.value = false },
        onActiveChange = { isSearchBarActiveState.value = it },
    ) {
        Spacer(Modifier.height(10.dp))
        filteredContent(shownItems.value, trackListState)
    }
}

@Composable
private fun SearchIcon(modifier: Modifier = Modifier) {
    val primaryColor = LocalAppColors.current.colorScheme.primary

    Icon(
        painter = painterResource(R.drawable.search_icon),
        contentDescription = null,
        tint = primaryColor,
        modifier = modifier
    )
}

@Composable
private inline fun CancelSearchIcon(
    queryState: StateFlow<String?>,
    isSearchBarActiveState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    crossinline setQuery: (String?) -> Unit
) {
    val primaryColor = LocalAppColors.current.colorScheme.primary

    Icon(
        painter = painterResource(R.drawable.cross),
        contentDescription = null,
        tint = primaryColor,
        modifier = modifier.clickable {
            when {
                queryState.value.isNullOrEmpty() -> isSearchBarActiveState.value = false
                else -> setQuery(null)
            }
        }
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

private inline fun <T> onFilterUpdate(
    allItems: List<T>,
    shownItems: MutableState<List<T>>,
    queryText: String?,
    filter: (query: String, item: T) -> Boolean
) {
    shownItems.value = when {
        queryText.isNullOrEmpty() -> allItems

        else -> {
            val query = queryText.lowercase()
            allItems.filter { track -> filter(query, track) }
        }
    }
}