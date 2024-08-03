package com.paranid5.crescendo.ui.drag

import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import arrow.core.curried
import com.paranid5.crescendo.utils.extensions.moved
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlin.math.absoluteValue

@Composable
internal fun <T> DraggingEffect(
    position: Float?,
    scrollingState: LazyListState,
    isDragging: Boolean,
    itemsState: MutableState<ImmutableList<T>>,
    currentDragIndexState: MutableIntState,
    draggedItemIndexState: MutableState<Int?>,
) {
    var draggedItemIndex by draggedItemIndexState
    val updPosition by rememberUpdatedState(position)
    val updDragging by rememberUpdatedState(isDragging)

    val listFlow = snapshotFlow { scrollingState.layoutInfo }
    val positionFlow = snapshotFlow { updPosition }
    var nearOffset by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(listFlow, positionFlow) {
        combine(listFlow, positionFlow) { listState, draggedCenter ->
            draggedCenter?.let(::nearestVisibleItem.curried()(listState))?.index
        }.collectLatest { near ->
            nearOffset = near
        }
    }

    LaunchedEffect(nearOffset) {
        if (updDragging)
            draggedItemIndex = nextDragItemIndex(
                near = nearOffset,
                draggedItemIndex = draggedItemIndex,
                itemsState = itemsState,
                currentDragIndexState = currentDragIndexState,
            )
    }
}

private fun nearestVisibleItem(
    listState: LazyListLayoutInfo,
    draggedCenter: Float
) = listState.visibleItemsInfo.minByOrNull {
    (draggedCenter - (it.offset + it.size / 2F)).absoluteValue
}

private fun <T> nextDragItemIndex(
    near: Int?,
    draggedItemIndex: Int?,
    itemsState: MutableState<ImmutableList<T>>,
    currentDragIndexState: MutableIntState,
) = when {
    near == null -> null
    draggedItemIndex == null -> near

    else -> near.also { toInd ->
        itemsState.value = itemsState.value.moved(fromIdx = draggedItemIndex, toIdx = toInd)

        currentDragIndexState.intValue = getMoveIndexForCurrentItem(
            fromIndex = draggedItemIndex,
            toIndex = toInd,
            currentTrackDragIndex = currentDragIndexState.intValue,
        )
    }
}

private fun getMoveIndexForCurrentItem(
    fromIndex: Int,
    toIndex: Int,
    currentTrackDragIndex: Int,
): Int = when {
    fromIndex < toIndex -> when (currentTrackDragIndex) {
        fromIndex -> toIndex
        in 0 until fromIndex -> currentTrackDragIndex
        in fromIndex..toIndex -> currentTrackDragIndex - 1
        else -> currentTrackDragIndex
    }

    else -> when (currentTrackDragIndex) {
        fromIndex -> toIndex
        in 0 until toIndex -> currentTrackDragIndex
        in toIndex..fromIndex -> currentTrackDragIndex + 1
        else -> currentTrackDragIndex
    }
}
