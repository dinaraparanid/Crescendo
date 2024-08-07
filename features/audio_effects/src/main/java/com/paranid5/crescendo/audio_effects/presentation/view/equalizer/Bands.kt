package com.paranid5.crescendo.audio_effects.presentation.view.equalizer

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.audio_effects.presentation.AudioEffectsViewModel
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerData
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun Bands(
    pointsState: SnapshotStateList<Offset>,
    modifier: Modifier = Modifier,
    viewModel: AudioEffectsViewModel = koinViewModel(),
) {
    val equalizerData by viewModel.equalizerState.collectLatestAsState()
    val presentLvlsDbState = rememberPresentLvlsDbState(equalizerData)

    Row(modifier) {
        Spacer(Modifier.width(10.dp))

        equalizerData?.bandLevels?.indices?.forEach {
            Band(
                index = it,
                presentLvlsDbState = presentLvlsDbState,
                pointsState = pointsState,
                modifier = Modifier.weight(1F)
            )

            Spacer(Modifier.width(10.dp))
        }
    }
}

@Composable
private fun rememberPresentLvlsDbState(equalizerData: EqualizerData?): SnapshotStateList<Float> {
    val equalizerPreset by remember(equalizerData?.currentPreset) {
        derivedStateOf { equalizerData?.currentPreset }
    }

    return remember(equalizerPreset) {
        mutableStateListOf(
            *equalizerData
                ?.bandLevels
                ?.map { it.toFloat() / EqualizerData.MILLIBELS_IN_DECIBEL }
                ?.toTypedArray()
                ?: emptyArray()
        )
    }
}