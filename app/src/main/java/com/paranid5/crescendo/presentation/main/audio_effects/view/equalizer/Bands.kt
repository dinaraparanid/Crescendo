package com.paranid5.crescendo.presentation.main.audio_effects.view.equalizer

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.EQUALIZER_DATA
import com.paranid5.crescendo.domain.eq.EqualizerData
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun Bands(
    viewModel: AudioEffectsViewModel,
    pointsState: SnapshotStateList<Offset>,
    modifier: Modifier = Modifier,
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA))
) {
    val equalizerData by equalizerDataState.collectAsState()
    val presentLvlsDbState = rememberPresentLvlsDbState(equalizerData!!)

    Row(modifier) {
        Spacer(Modifier.width(10.dp))

        equalizerData!!.bandLevels.indices.forEach {
            Band(
                index = it,
                presentLvlsDbState = presentLvlsDbState,
                pointsState = pointsState,
                viewModel = viewModel,
                modifier = Modifier.weight(1F)
            )

            Spacer(Modifier.width(10.dp))
        }
    }
}

@Composable
private fun rememberPresentLvlsDbState(equalizerData: EqualizerData): SnapshotStateList<Float> {
    val equalizerPreset by remember(equalizerData) {
        derivedStateOf { equalizerData.currentPreset }
    }

    return remember(equalizerPreset) {
        mutableStateListOf(*equalizerData.bandLevels.map { it / 1000F }.toTypedArray())
    }
}