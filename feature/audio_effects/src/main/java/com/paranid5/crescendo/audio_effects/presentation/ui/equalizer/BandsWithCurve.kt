package com.paranid5.crescendo.audio_effects.presentation.ui.equalizer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsState
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsUiIntent

@Composable
internal fun BandsWithCurve(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pointsState = remember(state.bandsAmount) {
        mutableStateListOf(*Array(state.bandsAmount) { Offset.Zero })
    }

    Box(modifier) {
        BandsCurve(
            pointsState = pointsState,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
        )

        Bands(
            state = state,
            onUiIntent = onUiIntent,
            pointsState = pointsState,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
        )
    }
}
