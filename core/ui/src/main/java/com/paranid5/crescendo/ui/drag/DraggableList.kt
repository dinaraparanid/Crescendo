package com.paranid5.crescendo.ui.drag

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import arrow.core.curried
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val ZeroOffset = 0F
private const val UpSpeedUp = 2F
private const val DownSpeedUp = 3F
private const val DragEventTimeout = 500L

typealias DraggableListItemContent<T> = @Composable (
    items: ImmutableList<T>,
    index: Int,
    currentTrackIndexAfterDrag: Int,
    modifier: Modifier,
) -> Unit

@Composable
fun <T> DraggableList(
    items: ImmutableList<T>,
    currentItemIndex: Int,
    onDismissed: (index: Int, item: T) -> Boolean,
    onDragged: (draggedItems: ImmutableList<T>, currentTrackIndexAfterDrag: Int) -> Unit,
    modifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
    scrollingState: LazyListState = rememberLazyListState(),
    key: ((index: Int, item: T) -> Any)? = null,
    itemContent: DraggableListItemContent<T>,
) {
    val draggableItemsState = remember(items) {
        mutableStateOf(items)
    }

    val draggableItems by draggableItemsState

    val currentItemIndexAfterDragState = remember(currentItemIndex) {
        mutableIntStateOf(currentItemIndex)
    }

    val currentItemIndexAfterDrag by currentItemIndexAfterDragState

    val positionState = remember { mutableStateOf<Float?>(null) }
    val position by positionState

    val draggedItemIndexState = remember { mutableStateOf<Int?>(null) }
    val draggedItemIndex by draggedItemIndexState

    val isDraggingState = remember { mutableStateOf(false) }
    val isDragging by isDraggingState

    val indexWithOffset by rememberItemIndexWithOffset(
        position = position,
        scrollingState = scrollingState,
        draggedItemIndex = draggedItemIndex,
    )

    DraggingEffect(
        position = position,
        scrollingState = scrollingState,
        isDragging = isDragging,
        itemsState = draggableItemsState,
        currentDragIndexState = currentItemIndexAfterDragState,
        draggedItemIndexState = draggedItemIndexState,
    )

    DismissibleList(
        items = draggableItems,
        scrollingState = scrollingState,
        onDismissed = onDismissed,
        modifier = modifier.handleItemsMovement(
            items = draggableItems,
            currentItemIndexAfterDrag = currentItemIndexAfterDrag,
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
                    itemContent(itemList2, index2, currentItemIndexAfterDrag, itemMod2)
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
private fun <T> Modifier.handleItemsMovement(
    items: ImmutableList<T>,
    currentItemIndexAfterDrag: Int,
    scrollingState: LazyListState,
    positionState: MutableState<Float?>,
    isDraggingState: MutableState<Boolean>,
    draggedItemIndexState: MutableState<Int?>,
    onDragged: (draggedItems: ImmutableList<T>, currentItemIndexAfterDrag: Int) -> Unit,
): Modifier {
    val draggedItems by rememberUpdatedState(items)
    val curTrackIndexAfterDrag by rememberUpdatedState(currentItemIndexAfterDrag)

    var position by positionState
    var isDragging by isDraggingState
    var draggedItemIndex by draggedItemIndexState

    val coroutineScope = rememberCoroutineScope()
    var itemOffset by remember { mutableStateOf(Offset.Zero) }
    var itemOffsetHandled by remember { mutableStateOf(false) }
    var overscrollJob by remember { mutableStateOf<Job?>(null) }

    val dragEventTrigger by produceDragEventTrigger()

    LaunchedEffect(isDragging, position, itemOffset, dragEventTrigger) {
        val itemSize = firstVisibleItem(scrollingState, itemOffset)?.size ?: 0
        val viewportHeight = scrollingState.layoutInfo.viewportSize.height.toFloat()

        position = position?.plus(itemOffset.y)?.coerceIn(
            minimumValue = ZeroOffset,
            maximumValue = viewportHeight - itemSize,
        )

        if (isDragging && overscrollJob?.isActive != true) {
            checkForOverscroll(
                scrollingState = scrollingState,
                offset = itemOffset,
                dragItemPosition = position,
            )?.let { overScrollOffset ->
                overscrollJob = coroutineScope.launch {
                    scrollingState.animateScrollBy(
                        value = overScrollOffset,
                        animationSpec = tween(easing = FastOutLinearInEasing),
                    )
                }
            }
        }

        itemOffsetHandled = true
    }

    LaunchedEffect(itemOffsetHandled) {
        if (itemOffsetHandled) {
            itemOffset = Offset.Zero
            itemOffsetHandled = false
        }
    }

    return this then Modifier.pointerInput(Unit) {
        detectDragGesturesAfterLongPress(
            onDrag = { change, offset ->
                change.consume()
                itemOffset = offset
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
                overscrollJob?.cancel()
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
    modifier: Modifier = Modifier,
) {
    val offset by rememberItemOffset(indexWithOffset, index)

    itemView(
        items,
        index,
        modifier
            .zIndex(offset?.let { 1F } ?: 0F)
            .graphicsLayer { translationY = offset ?: ZeroOffset }
            .clip(RoundedCornerShape(dimensions.padding.extraMedium))
            .background(
                offset
                    ?.let { colors.background.highContrast.copy(alpha = 0.5F) }
                    ?: Color.Transparent
            ),
    )
}

@Composable
private fun rememberItemOffset(indexWithOffset: Pair<Int, Float>?, index: Int) =
    remember(indexWithOffset, index) {
        derivedStateOf {
            indexWithOffset?.takeIf { it.first == index }?.second
        }
    }

@Composable
private fun produceDragEventTrigger() = produceState(0L) {
    while (true) {
        delay(DragEventTimeout)
        value = System.currentTimeMillis()
    }
}

private fun itemIndexWithOffset(
    position: Float?,
    scrollingState: LazyListState,
    draggedItemIndex: Int,
): Pair<Int, Float>? = nullable {
    val item = scrollingState
        .layoutInfo
        .visibleItemsInfo
        .getOrNull(draggedItemIndex - scrollingState.firstVisibleItemIndex)
        .bind()

    val offset = (position ?: ZeroOffset) - item.offset - item.size / 2F
    item.index to offset
}

private fun firstVisibleItem(scrollingState: LazyListState, offset: Offset) =
    scrollingState
        .layoutInfo
        .visibleItemsInfo
        .firstOrNull { offset.y.toInt() in it.offset..it.offset + it.size }

private fun checkForOverscroll(
    scrollingState: LazyListState,
    offset: Offset,
    dragItemPosition: Float?,
    upSpeedUp: Float = UpSpeedUp,
    downSpeedUp: Float = DownSpeedUp,
): Float? = nullable {
    val firstVisibleItem = firstVisibleItem(scrollingState, offset).bind()
    val itemSize = firstVisibleItem.size

    val startOffset = dragItemPosition.bind()
    val endOffset = firstVisibleItem.offsetEnd + startOffset

    when {
        startOffset > ZeroOffset ->
            (endOffset - scrollingState.layoutInfo.viewportEndOffset + itemSize)
                .takeIf { diff -> diff > ZeroOffset }
                ?.let { it * downSpeedUp }

        else ->
            (startOffset - scrollingState.layoutInfo.viewportStartOffset - itemSize)
                .takeIf { diff -> diff < ZeroOffset }
                ?.let { it * upSpeedUp }
    }
}
