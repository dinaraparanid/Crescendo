package com.paranid5.crescendo.presentation.main.audio_effects.view.equalizer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.utils.Spinner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun PresetSpinner(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA)),
    audioEffectsUIHandler: AudioEffectsUIHandler = koinInject()
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current

    val audioStatus by viewModel.audioStatusFlow.collectAsState(initial = null)
    val equalizerData by equalizerDataState.collectAsState()

    val customPresetIndex by remember(equalizerData) {
        derivedStateOf { equalizerData!!.presets.size }
    }

    val bandsPreset by remember(equalizerData) {
        derivedStateOf { equalizerData!!.bandsPreset }
    }

    val isCustomPreset by remember(bandsPreset) {
        derivedStateOf { bandsPreset == EqualizerBandsPreset.CUSTOM }
    }

    var selectedItemInd by remember(bandsPreset) {
        mutableIntStateOf(
            when (bandsPreset) {
                EqualizerBandsPreset.CUSTOM -> customPresetIndex
                EqualizerBandsPreset.BUILT_IN -> equalizerData!!.currentPreset.toInt()
                EqualizerBandsPreset.NIL -> 0
            }
        )
    }

    val curItemInd by remember(isCustomPreset) {
        derivedStateOf { if (isCustomPreset) customPresetIndex else selectedItemInd }
    }

    Box(modifier) {
        Spinner(
            items = equalizerData!!.presets.toList() + stringResource(R.string.custom),
            selectedItemIndexes = listOf(curItemInd),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart),
            onItemSelected = { ind, _ ->
                selectedItemInd = ind

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

        Image(
            painter = painterResource(R.drawable.arrow_down),
            contentDescription = stringResource(R.string.eq_presets),
            colorFilter = ColorFilter.tint(colors.primary),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(20.dp)
        )
    }
}