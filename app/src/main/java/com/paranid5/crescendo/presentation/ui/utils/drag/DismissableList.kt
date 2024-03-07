package com.paranid5.crescendo.presentation.ui.utils.drag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

@Composable
internal inline fun <T> DismissableList(
    items: ImmutableList<T>,
    scrollingState: LazyListState,
    crossinline onDismissed: (index: Int, item: T) -> Boolean,
    crossinline itemView: ListItemView<T>,
    modifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
    noinline key: ((index: Int, item: T) -> Any)? = null,
) = LazyColumn(
    verticalArrangement = Arrangement.spacedBy(10.dp),
    state = scrollingState,
    modifier = modifier
) {
    itemsIndexed(items, key) { index, _ ->
        DismissableItem(
            items = items,
            index = index,
            onDismissed = onDismissed,
            itemView = itemView,
            itemModifier = itemModifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private inline fun <T> DismissableItem(
    items: ImmutableList<T>,
    index: Int,
    crossinline onDismissed: (index: Int, item: T) -> Boolean,
    crossinline itemView: ListItemView<T>,
    modifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier
) {
    val item = items[index]

    val dismissState = rememberDismissState(
        index = index,
        item = item,
        onDismissed = onDismissed
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {},
        modifier = modifier,
        content = { itemView(items, index, itemModifier) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private inline fun <T> rememberDismissState(
    index: Int,
    item: T,
    crossinline onDismissed: (index: Int, item: T) -> Boolean,
) = rememberSwipeToDismissBoxState(
    confirmValueChange = {
        when (it) {
            SwipeToDismissBoxValue.StartToEnd -> onDismissed(index, item)
            else -> false
        }
    }
)