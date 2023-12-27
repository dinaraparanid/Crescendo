package com.paranid5.crescendo.presentation.ui.utils.drag

import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import arrow.core.curried
import com.paranid5.crescendo.domain.utils.extensions.move
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.absoluteValue

@Composable
fun <T> DraggingEffect(
    position: Float?,
    scrollingState: LazyListState,
    isDragging: Boolean,
    itemsState: MutableState<List<T>>,
    currentDragIndexState: MutableIntState,
    draggedItemIndexState: MutableState<Int?>,
) {
    var draggedItemIndex by draggedItemIndexState

    val listFlow = snapshotFlow { scrollingState.layoutInfo }

    val positionFlow = remember {
        snapshotFlow { position }.distinctUntilChanged()
    }

    LaunchedEffect(Unit) {
        listFlow
            .combine(positionFlow) { listState, draggedCenter ->
                draggedCenter
                    ?.let(::nearestVisibleItem.curried()(listState))
                    ?.index
            }
            .collect { near ->
                if (isDragging)
                    draggedItemIndex = nextDragItemIndex(
                        near = near,
                        draggedItemIndex = draggedItemIndex,
                        itemsState = itemsState,
                        currentDragIndexState = currentDragIndexState
                    )
            }
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
    itemsState: MutableState<List<T>>,
    currentDragIndexState: MutableIntState,
) = when {
    near == null -> null
    draggedItemIndex == null -> near

    else -> near.also { toInd ->
        itemsState.value = itemsState.value.movedItems(draggedItemIndex, toInd)

        currentDragIndexState.intValue = getMoveForCurItem(
            fromIndex = draggedItemIndex,
            toIndex = toInd,
            currentTrackDragIndex = currentDragIndexState.intValue
        )
    }
}

private fun <T> List<T>.movedItems(draggedItemIndex: Int, toIndex: Int, ) =
    toMutableList().apply { move(fromIdx = draggedItemIndex, toIdx = toIndex) }

private fun getMoveForCurItem(
    fromIndex: Int,
    toIndex: Int,
    currentTrackDragIndex: Int
): Int {
    return when {
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
}