package com.paranid5.crescendo.ui.drag

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import arrow.core.curried
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import kotlinx.collections.immutable.ImmutableList

typealias DraggableListItemContent<T> = @Composable (
    items: ImmutableList<T>,
    index: Int,
    currentTrackIndexAfterDrag: Int,
    modifier: Modifier,
) -> Unit

@NonRestartableComposable
@Composable
fun <T> DraggableList(
    items: ImmutableList<T>,
    currentItemIndex: Int,
    onDismissed: (index: Int, item: T) -> Boolean,
    onDragged: (draggedItems: ImmutableList<T>, currentTrackIndexAfterDrag: Int) -> Unit,
    modifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
    scrollingState: LazyListState = rememberLazyListState(),
    bottomPadding: Dp = dimensions.padding.extraMedium,
    key: ((index: Int, item: T) -> Any)? = null,
    itemContent: DraggableListItemContent<T>,
) {
    val draggableItemsState = remember(items) {
        mutableStateOf(items)
    }

    val draggableItems by draggableItemsState

    val currentTrackIndexAfterDragState = remember(currentItemIndex) {
        mutableIntStateOf(currentItemIndex)
    }

    val currentTrackIndexAfterDrag by currentTrackIndexAfterDragState

    val positionState = remember { mutableStateOf<Float?>(null) }
    val position by positionState

    val draggedItemIndexState = remember { mutableStateOf<Int?>(null) }
    val draggedItemIndex by draggedItemIndexState

    val isDraggingState = remember { mutableStateOf(false) }
    val isDragging by isDraggingState

    val indexWithOffset by rememberItemIndexWithOffset(
        position = position,
        scrollingState = scrollingState,
        draggedItemIndex = draggedItemIndex
    )

    DraggingEffect(
        position = position,
        scrollingState = scrollingState,
        isDragging = isDragging,
        itemsState = draggableItemsState,
        currentDragIndexState = currentTrackIndexAfterDragState,
        draggedItemIndexState = draggedItemIndexState,
    )

    DismissibleList(
        items = draggableItems,
        scrollingState = scrollingState,
        bottomPadding = bottomPadding,
        onDismissed = onDismissed,
        modifier = modifier.handleTracksMovement(
            items = draggableItems,
            currentTrackIndexAfterDrag = currentTrackIndexAfterDrag,
            scrollingState = scrollingState,
            positionState = positionState,
            isDraggingState = isDraggingState,
            draggedItemIndexState = draggedItemIndexState,
            onDragged = onDragged,
        ),
        itemModifier = itemModifier,
        key = key,
        itemContent = { itemList, index, draggableItemModifier ->
            DraggableItemList(
                items = itemList,
                index = index,
                indexWithOffset = indexWithOffset,
                itemView = { itemList2, index2, itemMod2 ->
                    itemContent(itemList2, index2, currentTrackIndexAfterDrag, itemMod2)
                },
                modifier = draggableItemModifier then itemModifier
            )
        },
    )
}

@Composable
private fun rememberItemIndexWithOffset(
    position: Float?,
    scrollingState: LazyListState,
    draggedItemIndex: Int?
) = remember(draggedItemIndex, position) {
    derivedStateOf {
        draggedItemIndex?.let(
            ::itemIndexWithOffset.curried()(position)(scrollingState)
        )
    }
}

@Composable
private fun <T> Modifier.handleTracksMovement(
    items: ImmutableList<T>,
    currentTrackIndexAfterDrag: Int,
    scrollingState: LazyListState,
    positionState: MutableState<Float?>,
    isDraggingState: MutableState<Boolean>,
    draggedItemIndexState: MutableState<Int?>,
    onDragged: (draggedItems: ImmutableList<T>, currentTrackIndexAfterDrag: Int) -> Unit,
): Modifier {
    val draggedItems by rememberUpdatedState(items)
    val curTrackIndexAfterDrag by rememberUpdatedState(currentTrackIndexAfterDrag)

    var position by positionState
    var isDragging by isDraggingState
    var draggedItemIndex by draggedItemIndexState

    return this then Modifier.pointerInput(Unit) {
        detectDragGesturesAfterLongPress(
            onDrag = { change, offset ->
                change.consume()
                position = position?.plus(offset.y)
            },
            onDragStart = { offset ->
                isDragging = true

                val firstVisibleItem = firstVisibleItem(scrollingState, offset)
                    ?: return@detectDragGesturesAfterLongPress

                position = firstVisibleItem.offset + firstVisibleItem.size / 2F
            },
            onDragEnd = {
                isDragging = false
                onDragged(draggedItems, curTrackIndexAfterDrag)
                draggedItemIndex = null
            },
        )
    }
}

@Composable
private inline fun <T> DraggableItemList(
    items: ImmutableList<T>,
    index: Int,
    indexWithOffset: Pair<Int, Float>?,
    crossinline itemView: ListItemContent<T>,
    modifier: Modifier = Modifier
) {
    val offset by rememberItemOffset(indexWithOffset, index)

    itemView(
        items,
        index,
        modifier
            .zIndex(offset?.let { 1F } ?: 0F)
            .graphicsLayer { translationY = offset ?: 0F }
    )
}

@Composable
private fun rememberItemOffset(indexWithOffset: Pair<Int, Float>?, index: Int) =
    remember(indexWithOffset, index) {
        derivedStateOf {
            indexWithOffset?.takeIf { it.first == index }?.second
        }
    }

private fun itemIndexWithOffset(
    position: Float?,
    scrollingState: LazyListState,
    draggedItemIndex: Int
): Pair<Int, Float>? {
    val item = scrollingState
        .layoutInfo
        .visibleItemsInfo
        .getOrNull(draggedItemIndex - scrollingState.firstVisibleItemIndex)
        ?: return null

    val offset = (position ?: 0F) - item.offset - item.size / 2F
    return item.index to offset
}

private fun firstVisibleItem(scrollingState: LazyListState, offset: Offset) =
    scrollingState
        .layoutInfo
        .visibleItemsInfo
        .firstOrNull { offset.y.toInt() in it.offset..it.offset + it.size }