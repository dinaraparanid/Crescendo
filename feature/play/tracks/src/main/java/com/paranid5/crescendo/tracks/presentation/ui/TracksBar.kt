package com.paranid5.crescendo.tracks.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.tracks.presentation.ui.bar.TotalLabel
import com.paranid5.crescendo.tracks.presentation.ui.bar.TrackOrderSpinner
import com.paranid5.crescendo.tracks.view_model.TracksState
import com.paranid5.crescendo.tracks.view_model.TracksUiIntent

private val SpinnerSize = 40.dp

@Composable
internal fun TracksBar(
    state: TracksState,
    onUiIntent: (TracksUiIntent) -> Unit,
    modifier: Modifier = Modifier
) = Row(modifier) {
    TotalLabel(
        state = state,
        modifier = Modifier.align(Alignment.CenterVertically)
    )

    TrackOrderSpinner(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier
            .align(Alignment.CenterVertically)
            .fillMaxWidth()
            .height(SpinnerSize)
    )
}