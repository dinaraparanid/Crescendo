package com.paranid5.crescendo.tracks.presentation.ui.bar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.common.tracks.TrackOrder
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.tracks.view_model.TracksState
import com.paranid5.crescendo.tracks.view_model.TracksUiIntent
import com.paranid5.crescendo.ui.utils.Spinner
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun TrackOrderSpinner(
    state: TracksState,
    onUiIntent: (TracksUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val trackOrder by remember(state) {
        derivedStateOf { state.trackOrder }
    }

    val selectedItemIndexes by rememberSelectedItemIndexes(trackOrder)

    val spinnerItems = persistentListOf(
        stringResource(R.string.title),
        stringResource(R.string.artist),
        stringResource(R.string.album),
        stringResource(R.string.date),
        stringResource(R.string.number_in_album),
        stringResource(R.string.asc),
        stringResource(R.string.desc),
    )

    Box(modifier) {
        TrackOrderSpinnerImpl(
            currentTrackOrder = trackOrder,
            spinnerItems = spinnerItems,
            selectedItemIndexes = selectedItemIndexes,
            onUiIntent = onUiIntent,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart),
        )

        TrackOrderLabel(
            trackOrder = trackOrder,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun TrackOrderSpinnerImpl(
    currentTrackOrder: TrackOrder,
    spinnerItems: ImmutableList<String>,
    selectedItemIndexes: ImmutableList<Int>,
    onUiIntent: (TracksUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Spinner(
    items = spinnerItems,
    selectedItemIndices = selectedItemIndexes,
    modifier = modifier,
    onItemSelected = { ind, _ ->
        onUiIntent(
            TracksUiIntent.UpdateState.UpdateTrackOrder(
                selectedTrackOrder(ind, currentTrackOrder)
            )
        )
    },
    previewItemFactory = { _, _, _ -> },
    selectedItemFactory = { _, item, mod ->
        SelectedOrderItem(item, mod.fillMaxWidth())
    },
    dropdownItemFactory = { _, item, mod ->
        OrderItem(item, mod.fillMaxWidth())
    },
)

@Composable
private fun rememberSelectedItemIndexes(trackOrder: TrackOrder) =
    remember(trackOrder) {
        derivedStateOf {
            val firstInd = trackOrder.contentOrder.ordinal
            val contentOrders = TrackOrder.TrackContentOrder.entries
            val secondInd = trackOrder.orderType.ordinal + contentOrders.size
            persistentListOf(firstInd, secondInd)
        }
    }

private fun selectedTrackOrder(
    selectedIndex: Int,
    currentTrackOrder: TrackOrder,
): TrackOrder {
    val contentOrders = TrackOrder.TrackContentOrder.entries
    val orderTypes = TrackOrder.TrackOrderType.entries
    val contentOrdersNum = contentOrders.size

    return when {
        selectedIndex < contentOrdersNum -> {
            val newContentOrder = contentOrders[selectedIndex]
            TrackOrder(
                contentOrder = newContentOrder,
                orderType = currentTrackOrder.orderType,
            )
        }

        else -> {
            val newOrderType = orderTypes[selectedIndex - contentOrdersNum]
            TrackOrder(
                contentOrder = currentTrackOrder.contentOrder,
                orderType = newOrderType,
            )
        }
    }
}
