package com.paranid5.mediastreamer.presentation.audio_effects

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.EQUALIZER_DATA
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.data.eq.EqualizerData
import com.paranid5.mediastreamer.data.eq.EqualizerParameters
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import com.paranid5.mediastreamer.presentation.ui.utils.Spinner
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
internal fun Equalizer(modifier: Modifier = Modifier) = Column(modifier) {
    PresetSpinner(Modifier.fillMaxWidth())
    Bands(Modifier.fillMaxWidth())
}

@Composable
private fun PresetSpinner(
    modifier: Modifier = Modifier,
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA)),
    audioEffectsUIHandler: AudioEffectsUIHandler = koinInject()
) {
    val context = LocalContext.current
    val primaryColor = LocalAppColors.current.value.primary

    val equalizerData by equalizerDataState.collectAsState()
    val customPresetIndex by remember { derivedStateOf { equalizerData!!.presets.size } }
    val equalizerParam by remember { derivedStateOf { equalizerData!!.currentParameter } }
    val isEQParamBands by remember { derivedStateOf { equalizerParam == EqualizerParameters.BANDS } }

    var selectedItemInd by remember {
        mutableStateOf(
            when (equalizerData!!.currentParameter) {
                EqualizerParameters.BANDS -> customPresetIndex
                EqualizerParameters.PRESET -> equalizerData!!.currentPreset.toInt()
                EqualizerParameters.NIL -> 0
            }
        )
    }

    val curItemInd by remember {
        derivedStateOf { if (isEQParamBands) customPresetIndex else selectedItemInd }
    }

    Box(modifier) {
        Spinner(
            items = equalizerData!!.presets.toList() + stringResource(R.string.custom),
            selectedItemInd = curItemInd,
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterStart),
            onItemSelected = { ind, _ ->
                selectedItemInd = ind

                when (ind) {
                    customPresetIndex -> audioEffectsUIHandler.switchToBandsAsync(context)

                    else -> audioEffectsUIHandler.storeEQPresetAsync(
                        context = context,
                        preset = ind.toShort()
                    )
                }
            }
        )

        Image(
            painter = painterResource(R.drawable.arrow_down),
            contentDescription = stringResource(R.string.eq_presets),
            colorFilter = ColorFilter.tint(primaryColor),
            modifier = Modifier.align(Alignment.CenterEnd).height(20.dp)
        )
    }
}

@Composable
private fun Bands(
    modifier: Modifier = Modifier,
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA))
) {
    val equalizerData by equalizerDataState.collectAsState()
    val bandLevelsRng by remember { derivedStateOf { equalizerData!!.bandLevels.indices } }

    Row(modifier) {
        bandLevelsRng.forEach {
            Band(index = it, modifier = Modifier.weight(1F))
        }
    }
}

@Composable
private fun Band(
    index: Int,
    modifier: Modifier = Modifier,
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA)),
    audioEffectsUIHandler: AudioEffectsUIHandler = koinInject()
) {
    val context = LocalContext.current

    val equalizerData by equalizerDataState.collectAsState()
    val equalizerParam by remember { derivedStateOf { equalizerData!!.currentParameter } }
    val isEQParamBands by remember { derivedStateOf { equalizerParam == EqualizerParameters.BANDS } }

    val curLevel by remember { derivedStateOf { equalizerData!!.bandLevels[index] / 1000F } }
    var levelDb by remember { mutableStateOf(curLevel) }
    val minDb by remember { derivedStateOf { equalizerData!!.minBandLevel / 1000F } }
    val maxDb by remember { derivedStateOf { equalizerData!!.maxBandLevel / 1000F } }

    Column(
        modifier
            .graphicsLayer {
                rotationZ = 270F
                transformOrigin = TransformOrigin(0F, 0F)
            }
            .layout { measurable, constraints ->
                val placeable = measurable.measure(
                    Constraints(
                        minWidth = constraints.minHeight,
                        maxWidth = constraints.maxHeight,
                        minHeight = constraints.minWidth,
                        maxHeight = constraints.maxHeight,
                    )
                )

                layout(placeable.height, placeable.width) {
                    placeable.place(-placeable.width, 0)
                }
            }
    ) {
        Slider(
            value = if (isEQParamBands) levelDb else curLevel,
            valueRange = minDb..maxDb,
            onValueChange = { level ->
                levelDb = level

                val newLevels = equalizerData!!.bandLevels.toMutableList().also {
                    it[index] = (level * 1000).toInt().toShort()
                }

                audioEffectsUIHandler.storeEQBandsAsync(context, newLevels)
            }
        )
    }
}