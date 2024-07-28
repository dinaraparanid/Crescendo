package com.paranid5.crescendo.audio_effects.presentation.view.equalizer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.paranid5.crescendo.audio_effects.presentation.AudioEffectsViewModel
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BandsWithCurve(
    modifier: Modifier = Modifier,
    viewModel: AudioEffectsViewModel = koinViewModel(),
) {
    val equalizerData by viewModel.equalizerState.collectLatestAsState()

    val pointsState = remember {
        mutableStateListOf(*Array(equalizerData?.bandLevels?.size ?: 0) { Offset.Zero })
    }

    Box(modifier) {
        BandsCurve(
            pointsState = pointsState,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        )

        Bands(
            pointsState = pointsState,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        )
    }
}