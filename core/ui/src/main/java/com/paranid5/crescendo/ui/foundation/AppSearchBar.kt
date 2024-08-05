package com.paranid5.crescendo.ui.foundation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

typealias FilteredContent<T> = @Composable ColumnScope.(
    filtered: ImmutableList<T>,
    scrollingState: LazyListState,
) -> Unit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AppSearchBar(
    items: ImmutableList<T>,
    shownItems: ImmutableList<T>,
    setFilteredItems: (ImmutableList<T>) -> Unit,
    query: String?,
    setQuery: (String?) -> Unit,
    isSearchBarActive: Boolean,
    setSearchBarActive: (Boolean) -> Unit,
    filter: (query: String, item: T) -> Boolean,
    modifier: Modifier = Modifier,
    filteredContent: FilteredContent<T>
) {
    val scrollListScope = rememberCoroutineScope()
    val trackListState = rememberLazyListState()

    LaunchedEffect(items, query) {
        setFilteredItems(filteredItems(items, query, filter))
    }

    SearchBar(
        modifier = modifier,
        query = query.orEmpty(),
        leadingIcon = { SearchIcon() },
        trailingIcon = {
            if (isSearchBarActive)
                CancelSearchIcon(
                    query = query,
                    setQuery = setQuery,
                    setSearchBarActive = setSearchBarActive,
                )
        },
        placeholder = {
            Text(
                text = stringResource(R.string.track_input_filter_hint),
                color = colors.text.primary
            )
        },
        colors = searcherColors,
        windowInsets = WindowInsets(0.dp),
        onQueryChange = { q ->
            setQuery(q)
            scrollListScope.launch { trackListState.scrollToItem(0) }
        },
        active = isSearchBarActive,
        onSearch = { setSearchBarActive(false) },
        onActiveChange = { setSearchBarActive(it) },
    ) {
        Spacer(Modifier.height(dimensions.padding.small))
        filteredContent(shownItems, trackListState)
    }
}

@Composable
private fun SearchIcon(modifier: Modifier = Modifier) =
    Icon(
        painter = painterResource(R.drawable.search),
        contentDescription = null,
        tint = colors.primary,
        modifier = modifier,
    )

@Composable
private inline fun CancelSearchIcon(
    query: String?,
    crossinline setSearchBarActive: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    crossinline setQuery: (String?) -> Unit
) = Icon(
    painter = painterResource(R.drawable.cross),
    contentDescription = null,
    tint = colors.primary,
    modifier = modifier.clickable {
        when {
            query.isNullOrEmpty() -> setSearchBarActive(false)
            else -> setQuery(null)
        }
    },
)

@OptIn(ExperimentalMaterial3Api::class)
private inline val searcherColors
    @Composable
    get() = SearchBarDefaults.colors(
        containerColor = Color.Transparent,
        inputFieldColors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.Gray,
        )
    )

private inline fun <T> filteredItems(
    allItems: ImmutableList<T>,
    queryText: String?,
    filter: (query: String, item: T) -> Boolean
) = when {
    queryText.isNullOrEmpty() -> allItems

    else -> {
        val query = queryText.lowercase()

        allItems
            .filter { track -> filter(query, track) }
            .toImmutableList()
    }
}