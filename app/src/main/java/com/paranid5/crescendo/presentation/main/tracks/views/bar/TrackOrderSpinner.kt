package com.paranid5.crescendo.presentation.main.tracks.views.bar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.domain.tracks.TrackOrder
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.tracks.TracksViewModel
import com.paranid5.crescendo.presentation.main.tracks.properties.compose.collectTrackOrderAsState
import com.paranid5.crescendo.presentation.ui.utils.Spinner
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@Composable
fun TrackOrderSpinner(modifier: Modifier = Modifier) {
    val selectedItemIndexes by rememberSelectedItemIndexes()

    val spinnerItems = persistentListOf(
        "${stringResource(R.string.by)} ${stringResource(R.string.title)}",
        "${stringResource(R.string.by)} ${stringResource(R.string.artist)}",
        "${stringResource(R.string.by)} ${stringResource(R.string.album)}",
        "${stringResource(R.string.by)} ${stringResource(R.string.date)}",
        "${stringResource(R.string.by)} ${stringResource(R.string.number_in_album)}",
        "${stringResource(R.string.by)} ${stringResource(R.string.asc)}",
        "${stringResource(R.string.by)} ${stringResource(R.string.desc)}",
    )

    Box(modifier) {
        TrackOrderSpinnerImpl(
            spinnerItems = spinnerItems,
            selectedItemIndexes = selectedItemIndexes,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart),
        )

        TrackOrderLabel(Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
private fun TrackOrderSpinnerImpl(
    spinnerItems: ImmutableList<String>,
    selectedItemIndexes: ImmutableList<Int>,
    modifier: Modifier = Modifier,
    viewModel: TracksViewModel = koinActivityViewModel(),
) {
    val scope = rememberCoroutineScope()
    val trackOrder by viewModel.collectTrackOrderAsState()

    Spinner(
        items = spinnerItems,
        selectedItemIndices = selectedItemIndexes,
        modifier = modifier,
        onItemSelected = { ind, _ ->
            scope.launch {
                viewModel.setTrackOrder(selectedTrackOrder(ind, trackOrder))
            }
        },
        previewItemFactory = { _, _, _ -> },
        selectedItemFactory = { _, item, mod ->
            SelectedOrderItem(item, mod.fillMaxWidth())
        },
        dropdownItemFactory = { _, item, mod ->
            OrderItem(item, mod.fillMaxWidth())
        }
    )
}

@Composable
private fun rememberSelectedItemIndexes(viewModel: TracksViewModel = koinActivityViewModel()): State<ImmutableList<Int>> {
    val trackOrder by viewModel.collectTrackOrderAsState()

    return remember(trackOrder) {
        derivedStateOf {
            val firstInd = trackOrder.contentOrder.ordinal
            val contentOrders = TrackOrder.TrackContentOrder.entries
            val secondInd = trackOrder.orderType.ordinal + contentOrders.size
            persistentListOf(firstInd, secondInd)
        }
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
            TrackOrder(newContentOrder, currentTrackOrder.orderType)
        }

        else -> {
            val newOrderType = orderTypes[selectedIndex - contentOrdersNum]
            TrackOrder(currentTrackOrder.contentOrder, newOrderType)
        }
    }
}