package com.paranid5.crescendo.audio_effects.presentation.view.equalizer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.audio_effects.presentation.AudioEffectsViewModel
import com.paranid5.crescendo.core.common.eq.EqualizerBandsPreset
import com.paranid5.crescendo.core.common.eq.EqualizerData
import com.paranid5.crescendo.core.impl.di.EQUALIZER_DATA
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.ui.utils.Spinner
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
internal fun PresetSpinner(modifier: Modifier = Modifier) =
    Box(modifier) {
        PresetSpinnerImpl(
            Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart),
        )

        PresetSpinnerArrow(
            Modifier
                .align(Alignment.CenterEnd)
                .size(24.dp)
                .padding(end = 8.dp)
        )
    }

@Composable
private fun PresetSpinnerImpl(
    modifier: Modifier = Modifier,
    viewModel: AudioEffectsViewModel = koinViewModel(),
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA)),
) {
    val equalizerData by equalizerDataState.collectLatestAsState()
    val customPresetIndex by rememberCustomPresetIndex(equalizerData)
    var selectedItemIndex by rememberSelectedItemIndex(equalizerData)
    val curItemIndex by rememberCurrentItemIndex(selectedItemIndex, equalizerData)
    val builtInPresets by rememberBuiltInPresets(equalizerData)
    val presets = (builtInPresets + stringResource(R.string.custom)).toImmutableList()
    val selectedItemIndices by rememberSelectedItemIndices(curItemIndex)

    com.paranid5.crescendo.ui.utils.Spinner(
        items = presets,
        selectedItemIndices = selectedItemIndices,
        modifier = modifier,
        onItemSelected = { ind, _ ->
            selectedItemIndex = ind

            when (ind) {
                customPresetIndex -> viewModel.viewModelScope.launch(Dispatchers.IO) {
                    viewModel.setEqualizerParam(EqualizerBandsPreset.CUSTOM)
                }

                else -> viewModel.viewModelScope.launch(Dispatchers.IO) {
                    viewModel.setEqualizerParam(EqualizerBandsPreset.BUILT_IN)
                    viewModel.setEqualizerPreset(ind.toShort())
                }
            }
        }
    )
}

@Composable
private fun PresetSpinnerArrow(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Image(
        painter = painterResource(R.drawable.arrow_down),
        contentDescription = stringResource(R.string.eq_presets),
        colorFilter = ColorFilter.tint(colors.primary),
        modifier = modifier
    )
}

@Composable
private fun rememberCustomPresetIndex(equalizerData: EqualizerData?) =
    remember(equalizerData?.presets) {
        derivedStateOf { equalizerData?.presets?.size }
    }

@Composable
private fun rememberBandsPreset(equalizerData: EqualizerData?) =
    remember(equalizerData?.bandsPreset) {
        derivedStateOf { equalizerData?.bandsPreset }
    }

@Composable
private fun rememberIsCustomPreset(equalizerData: EqualizerData?): State<Boolean> {
    val bandsPreset by rememberBandsPreset(equalizerData)

    return remember(bandsPreset) {
        derivedStateOf { bandsPreset == EqualizerBandsPreset.CUSTOM }
    }
}

@Composable
private fun rememberSelectedItemIndex(equalizerData: EqualizerData?): MutableState<Int> {
    val customPresetIndex by rememberCustomPresetIndex(equalizerData)
    val bandsPreset by rememberBandsPreset(equalizerData)

    return remember(bandsPreset) {
        mutableIntStateOf(
            when (bandsPreset) {
                EqualizerBandsPreset.CUSTOM -> customPresetIndex ?: 0
                EqualizerBandsPreset.BUILT_IN -> equalizerData?.currentPreset?.toInt() ?: 0
                EqualizerBandsPreset.NIL -> 0
                null -> 0
            }
        )
    }
}

@Composable
private fun rememberCurrentItemIndex(
    selectedItemIndex: Int,
    equalizerData: EqualizerData?
): State<Int?> {
    val isCustomPreset by rememberIsCustomPreset(equalizerData)
    val customPresetIndex by rememberCustomPresetIndex(equalizerData)

    return remember(selectedItemIndex, isCustomPreset) {
        derivedStateOf { if (isCustomPreset) customPresetIndex else selectedItemIndex }
    }
}

@Composable
private fun rememberBuiltInPresets(equalizerData: EqualizerData?) =
    remember(equalizerData?.presets) {
        derivedStateOf { equalizerData?.presets ?: persistentListOf() }
    }

@Composable
private fun rememberSelectedItemIndices(curItemIndex: Int?) =
    remember(curItemIndex) {
        derivedStateOf { persistentListOf(curItemIndex ?: 0) }
    }