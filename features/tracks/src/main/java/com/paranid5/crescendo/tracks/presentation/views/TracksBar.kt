package com.paranid5.crescendo.tracks.presentation.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.tracks.presentation.views.bar.TrackOrderSpinner
import com.paranid5.crescendo.tracks.presentation.views.bar.TracksNumberLabel

@Composable
internal fun TracksBar(modifier: Modifier = Modifier) =
    Row(modifier) {
        TracksNumberLabel(Modifier.align(Alignment.CenterVertically))

        TrackOrderSpinner(
            Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth()
                .height(40.dp)
        )
    }