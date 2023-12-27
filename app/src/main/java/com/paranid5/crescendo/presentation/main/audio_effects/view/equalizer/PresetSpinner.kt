package com.paranid5.crescendo.presentation.main.audio_effects.view.equalizer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.EQUALIZER_DATA
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.eq.EqualizerBandsPreset
import com.paranid5.crescendo.domain.eq.EqualizerData
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsUIHandler
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsViewModel
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.utils.Spinner
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun PresetSpinner(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
) = Box(modifier) {
    PresetSpinnerImpl(
        viewModel = viewModel,
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterStart),
    )

    PresetSpinnerArrow(
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .height(20.dp)
    )
}

@Composable
private fun PresetSpinnerImpl(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA)),
    audioEffectsUIHandler: AudioEffectsUIHandler = koinInject()
) {
    val context = LocalContext.current

    val audioStatus by viewModel
        .audioStatusFlow
        .collectLatestAsState(initial = null)

    val equalizerData by equalizerDataState.collectLatestAsState()

    val customPresetIndex by rememberCustomPresetIndex(equalizerData)
    var selectedItemIndex by rememberSelectedItemIndex(equalizerData)
    val curItemInd by rememberCurrentItemIndex(selectedItemIndex, equalizerData)
    val builtInPresets by rememberBuiltInPresets(equalizerData)

    Spinner(
        items = builtInPresets + stringResource(R.string.custom),
        selectedItemIndexes = listOf(curItemInd ?: 0),
        modifier = modifier,
        onItemSelected = { ind, _ ->
            selectedItemIndex = ind

            when (ind) {
                customPresetIndex -> viewModel.viewModelScope.launch {
                    audioEffectsUIHandler.switchToBands(
                        context = context,
                        audioStatus = audioStatus!!
                    )
                }

                else -> viewModel.viewModelScope.launch {
                    audioEffectsUIHandler.storeAndSwitchToPreset(
                        context = context,
                        preset = ind.toShort(),
                        audioStatus = audioStatus!!
                    )
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

    return remember(isCustomPreset) {
        derivedStateOf { if (isCustomPreset) customPresetIndex else selectedItemIndex }
    }
}

@Composable
private fun rememberBuiltInPresets(equalizerData: EqualizerData?) =
    remember(equalizerData?.presets) {
        derivedStateOf { equalizerData?.presets ?: persistentListOf() }
    }