package com.paranid5.crescendo.presentation.main.trimmer.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.presentation.main.trimmer.views.waveform.TrimmedDuration

@Composable
fun WaveformWithDurations(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier,
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO
) = Column(modifier) {
    WaveformWithPosition(
        viewModel = viewModel,
        spikeWidthRatio = spikeWidthRatio,
        modifier = Modifier
            .weight(1F)
            .align(Alignment.CenterHorizontally)
    )

    Spacer(Modifier.height(8.dp))

    TrimmedDuration(
        viewModel = viewModel,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
}