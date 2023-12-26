package com.paranid5.crescendo.presentation.main.tracks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.storeTrackOrder
import com.paranid5.crescendo.domain.tracks.TrackOrder
import com.paranid5.crescendo.presentation.ui.extensions.toString
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.utils.Spinner
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun TracksNumberOrderBar(
    tracksNumber: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current.colorScheme

    Row(modifier) {
        Text(
            text = "${stringResource(R.string.tracks)}: $tracksNumber",
            color = colors.primary,
            textAlign = TextAlign.Start,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        TrackOrderSpinner(
            Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth()
                .height(40.dp)
        )
    }
}

@Composable
private fun TrackOrderSpinner(
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject()
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current.colorScheme
    val scope = rememberCoroutineScope()
    val trackOrder by storageHandler.trackOrderState.collectAsState()

    val spinnerItems = listOf(
        stringResource(R.string.title),
        stringResource(R.string.artist),
        stringResource(R.string.album),
        stringResource(R.string.date),
        stringResource(R.string.number_in_album),
        stringResource(R.string.asc),
        stringResource(R.string.desc)
    ).map { "${stringResource(R.string.by)} $it" }

    val selectedItemIndexes by remember {
        derivedStateOf {
            val firstInd = trackOrder.contentOrder.ordinal
            val contentOrders = TrackOrder.TrackContentOrder.entries
            val secondInd = trackOrder.orderType.ordinal + contentOrders.size
            listOf(firstInd, secondInd)
        }
    }

    Box(modifier) {
        Spinner(
            items = spinnerItems,
            selectedItemIndexes = selectedItemIndexes,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart),
            onItemSelected = { ind, _ ->
                scope.launch {
                    storageHandler.storeTrackOrder(selectedTrackOrder(ind, trackOrder))
                }
            },
            previewItemFactory = { _, _, _ -> },
            selectedItemFactory = { _, item, modifier ->
                SelectedOrderItem(item, modifier.fillMaxWidth())
            },
            dropdownItemFactory = { _, item, modifier ->
                OrderItem(item, modifier.fillMaxWidth())
            }
        )

        Text(
            text = trackOrder.toString(context),
            color = colors.primary,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun SelectedOrderItem(item: String, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.colorScheme
    val primaryColor = colors.primary
    val backgroundColor = colors.background

    Row(modifier) {
        Checkbox(
            checked = true,
            onCheckedChange = null,
            modifier = Modifier.align(Alignment.CenterVertically),
            colors = CheckboxDefaults.colors(
                checkedColor = primaryColor,
                checkmarkColor = backgroundColor,
            ),
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = item,
            color = primaryColor,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}

@Composable
private fun OrderItem(item: String, modifier: Modifier = Modifier) {
    Row(modifier) {
        Checkbox(
            checked = false,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(uncheckedColor = Color.White),
        )

        Spacer(Modifier.width(10.dp))

        Text(text = item)
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