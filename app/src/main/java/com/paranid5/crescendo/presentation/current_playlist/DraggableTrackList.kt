package com.paranid5.crescendo.presentation.current_playlist

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import com.paranid5.crescendo.data.tracks.Track
import com.paranid5.crescendo.data.utils.extensions.move
import com.paranid5.crescendo.domain.StorageHandler
import com.paranid5.crescendo.presentation.tracks.TrackItemView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.math.absoluteValue

private const val TAG = "DraggableTrackList"

@Composable
internal inline fun DraggableTrackList(
    tracks: List<Track>,
    noinline onTrackDismissed: suspend (Int, Track) -> Boolean,
    crossinline onTrackDragged: suspend (List<Track>, Int) -> Unit,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    scrollingState: LazyListState = rememberLazyListState(),
    storageHandler: StorageHandler = koinInject(),
    noinline trackItemView: TrackItemView
) {
    val coroutineScope = rememberCoroutineScope()
    val positionState = remember { mutableStateOf<Float?>(null) }
    val draggedItemIndexState = remember { mutableStateOf<Int?>(null) }
    val isDraggingState = remember { mutableStateOf(false) }

    val draggableTracksState = remember { mutableStateOf(tracks) }
    val draggableTracks by draggableTracksState

    val currentTrackIndex by storageHandler.currentTrackIndexState.collectAsState()
    val currentTrackDragIndexState = remember { mutableIntStateOf(currentTrackIndex) }

    LaunchedEffect(key1 = tracks) {
        draggableTracksState.value = tracks
    }

    val indexWithOffset by remember {
        derivedStateOf {
            draggedItemIndexState
                .value
                ?.let {
                    scrollingState
                        .layoutInfo
                        .visibleItemsInfo
                        .getOrNull(it - scrollingState.firstVisibleItemIndex)
                }
                ?.let {
                    val offset = ((positionState.value ?: 0F) - it.offset - it.size / 2F)
                    it.index to offset
                }
        }
    }

    LaunchDraggingHandling(
        tracksState = draggableTracksState,
        scrollingState = scrollingState,
        positionState = positionState,
        isDraggingState = isDraggingState,
        draggedItemIndexState = draggedItemIndexState,
        currentTrackDragIndexState = currentTrackDragIndexState
    )

    DismissableTrackList(
        tracks = draggableTracks,
        scrollingState = scrollingState,
        onTrackDismissed = onTrackDismissed,
        modifier = modifier.handleTracksMovement(
            tracksState = draggableTracksState,
            currentTrackDragIndexState = currentTrackDragIndexState,
            scrollingState = scrollingState,
            positionState = positionState,
            isDraggingState = isDraggingState,
            draggedItemIndexState = draggedItemIndexState,
            coroutineScope = coroutineScope,
            onTrackDragged = onTrackDragged
        ),
        trackItemModifier = { ind ->
            val offset by remember {
                derivedStateOf { indexWithOffset?.takeIf { it.first == ind }?.second }
            }

            trackItemModifier
                .zIndex(offset?.let { 1F } ?: 0F)
                .graphicsLayer { translationY = offset ?: 0F }
        },
        trackItemView = trackItemView,
    )
}

@Composable
internal inline fun DraggableTrackList(
    tracks: List<Track>,
    noinline onTrackDismissed: suspend (Int, Track) -> Boolean,
    modifier: Modifier = Modifier,
    trackItemModifier: Modifier = Modifier,
    scrollingState: LazyListState = rememberLazyListState(),
    storageHandler: StorageHandler = koinInject(),
    crossinline onTrackDragged: suspend (List<Track>, Int) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val positionState = remember { mutableStateOf<Float?>(null) }
    val draggedItemIndexState = remember { mutableStateOf<Int?>(null) }
    val isDraggingState = remember { mutableStateOf(false) }

    val draggableTracksState = remember { mutableStateOf(tracks) }
    val draggableTracks by draggableTracksState

    val currentTrackIndex by storageHandler.currentTrackIndexState.collectAsState()
    val currentTrackDragIndexState = remember { mutableIntStateOf(currentTrackIndex) }

    LaunchedEffect(key1 = tracks) {
        draggableTracksState.value = tracks
    }

    val indexWithOffset by remember {
        derivedStateOf {
            draggedItemIndexState
                .value
                ?.let {
                    scrollingState
                        .layoutInfo
                        .visibleItemsInfo
                        .getOrNull(it - scrollingState.firstVisibleItemIndex)
                }
                ?.let {
                    val offset = ((positionState.value ?: 0F) - it.offset - it.size / 2F)
                    it.index to offset
                }
        }
    }

    LaunchDraggingHandling(
        tracksState = draggableTracksState,
        scrollingState = scrollingState,
        positionState = positionState,
        isDraggingState = isDraggingState,
        draggedItemIndexState = draggedItemIndexState,
        currentTrackDragIndexState = currentTrackDragIndexState
    )

    DismissableTrackList(
        tracks = draggableTracks,
        scrollingState = scrollingState,
        onTrackDismissed = onTrackDismissed,
        modifier = modifier.handleTracksMovement(
            tracksState = draggableTracksState,
            currentTrackDragIndexState = currentTrackDragIndexState,
            scrollingState = scrollingState,
            positionState = positionState,
            isDraggingState = isDraggingState,
            draggedItemIndexState = draggedItemIndexState,
            coroutineScope = coroutineScope,
            onTrackDragged = onTrackDragged
        ),
        trackItemModifier = { ind ->
            val offset by remember {
                derivedStateOf { indexWithOffset?.takeIf { it.first == ind }?.second }
            }

            trackItemModifier
                .zIndex(offset?.let { 1F } ?: 0F)
                .graphicsLayer { translationY = offset ?: 0F }
        },
    )
}

@Composable
private fun LaunchDraggingHandling(
    tracksState: MutableState<List<Track>>,
    currentTrackDragIndexState: MutableIntState,
    scrollingState: LazyListState,
    positionState: MutableState<Float?>,
    isDraggingState: MutableState<Boolean>,
    draggedItemIndexState: MutableState<Int?>,
) {
    val isDragging by isDraggingState
    var draggedItemIndex by draggedItemIndexState

    val listFlow = snapshotFlow { scrollingState.layoutInfo }

    val positionFlow = remember {
        snapshotFlow { positionState.value }.distinctUntilChanged()
    }

    LaunchedEffect(Unit) {
        listFlow
            .combine(positionFlow) { listState, pos ->
                pos
                    ?.let { draggedCenter ->
                        listState.visibleItemsInfo.minByOrNull {
                            (draggedCenter - (it.offset + it.size / 2F)).absoluteValue
                        }
                    }
                    ?.index
            }
            .collect { near ->
                if (isDragging) draggedItemIndex = when {
                    near == null -> null
                    draggedItemIndex == null -> near

                    else -> near.also { toInd ->
                        tracksState.value = tracksState.value.toMutableList().apply {
                            move(fromIdx = draggedItemIndex!!, toIdx = toInd)
                        }

                        currentTrackDragIndexState.intValue = getMoveForCurTrack(
                            currentTrackDragIndexState = currentTrackDragIndexState,
                            fromIdx = draggedItemIndex!!,
                            toIdx = toInd
                        )
                    }
                }
            }
    }
}

private fun getMoveForCurTrack(
    currentTrackDragIndexState: MutableIntState,
    fromIdx: Int,
    toIdx: Int
): Int {
    val cur = currentTrackDragIndexState.intValue
    Log.d(TAG, "from: $fromIdx to: $toIdx")

    return when {
        fromIdx < toIdx -> when (cur) {
            fromIdx -> toIdx
            in 0 until fromIdx -> cur
            in fromIdx..toIdx -> cur - 1
            else -> cur
        }

        else -> when (cur) {
            fromIdx -> toIdx
            in 0 until toIdx -> cur
            in toIdx..fromIdx -> cur + 1
            else -> cur
        }
    }
}

private inline fun Modifier.handleTracksMovement(
    tracksState: State<List<Track>>,
    currentTrackDragIndexState: MutableIntState,
    scrollingState: LazyListState,
    positionState: MutableState<Float?>,
    isDraggingState: MutableState<Boolean>,
    draggedItemIndexState: MutableState<Int?>,
    coroutineScope: CoroutineScope,
    crossinline onTrackDragged: suspend (List<Track>, Int) -> Unit,
) = this then Modifier.pointerInput(Unit) {
    val stopDragAndUpdateTrackList = {
        isDraggingState.value = false

        coroutineScope.launch {
            onTrackDragged(tracksState.value, currentTrackDragIndexState.intValue)
            draggedItemIndexState.value = null
        }
    }

    detectDragGesturesAfterLongPress(
        onDrag = { change, offset ->
            change.consume()
            positionState.value = positionState.value?.plus(offset.y)
        },
        onDragStart = { offset ->
            isDraggingState.value = true

            scrollingState
                .layoutInfo
                .visibleItemsInfo
                .firstOrNull { offset.y.toInt() in it.offset..it.offset + it.size }
                ?.also { positionState.value = it.offset + it.size / 2F }
        },
        onDragEnd = { stopDragAndUpdateTrackList() },
        onDragCancel = { stopDragAndUpdateTrackList() }
    )
}