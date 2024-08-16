package com.paranid5.crescendo.ui.drag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun <T> DismissibleList(
    items: ImmutableList<T>,
    scrollingState: LazyListState,
    onDismissed: (index: Int, item: T) -> Boolean,
    modifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(dimensions.padding.extraMedium),
    key: ((index: Int, item: T) -> Any)? = null,
    itemContent: ListItemContent<T>,
) {
    val itemSpace = remember(verticalArrangement) { verticalArrangement.spacing }

    LazyColumn(
        state = scrollingState,
        modifier = modifier,
    ) {
        itemsIndexed(items, key) { index, _ ->
            DismissibleItem(
                items = items,
                index = index,
                onDismissed = onDismissed,
                itemView = itemContent,
                itemModifier = itemModifier.fillMaxWidth()
            )

            if (index != items.lastIndex)
                Spacer(Modifier.height(itemSpace))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> DismissibleItem(
    items: ImmutableList<T>,
    index: Int,
    onDismissed: (index: Int, item: T) -> Boolean,
    modifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
    itemView: ListItemContent<T>
) {
    val item = remember(items, index) { items[index] }

    val dismissState = rememberDismissState(
        index = index,
        item = item,
        onDismissed = onDismissed,
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