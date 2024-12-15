package com.paranid5.crescendo.audio_effects.presentation.ui.equalizer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsState
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsUiIntent
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerData.Companion.MILLIBELS_IN_DECIBEL

@Composable
internal fun Bands(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    pointsState: SnapshotStateList<Offset>,
    modifier: Modifier = Modifier,
) {
    val presentLvlsDbState = rememberPresentLvlsDbState(state)

    Row(
        modifier = modifier.padding(horizontal = dimensions.padding.medium),
        horizontalArrangement = Arrangement.spacedBy(dimensions.padding.medium),
    ) {
        repeat(state.bandsAmount) { index ->
            Band(
                index = index,
                presentLvlsDbState = presentLvlsDbState,
                pointsState = pointsState,
                state = state,
                onUiIntent = onUiIntent,
                modifier = Modifier.weight(1F),
            )
        }
    }
}

@Suppress("SpreadOperator")
@Composable
private fun rememberPresentLvlsDbState(state: AudioEffectsState): SnapshotStateList<Float> =
    remember(state.equalizerUiState?.currentPreset) {
        mutableStateListOf(
            *state.equalizerUiState
                ?.bandLevels
                ?.map { it.toFloat() / MILLIBELS_IN_DECIBEL }
                ?.toTypedArray()
                ?: emptyArray()
        )
    }
