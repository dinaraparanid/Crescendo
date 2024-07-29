package com.paranid5.crescendo.audio_effects.presentation.view.equalizer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.audio_effects.domain.updatedEQBandLevels
import com.paranid5.crescendo.audio_effects.presentation.AudioEffectsViewModel
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerBandsPreset
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BandSlider(
    index: Int,
    presentLvlsDbState: SnapshotStateList<Float>,
    pointsState: SnapshotStateList<Offset>,
    minDb: Float,
    maxDb: Float,
    sliderYPosState: MutableFloatState,
    sliderWidthState: MutableIntState,
    sliderHeightState: MutableIntState,
    equalizerData: EqualizerData?,
    modifier: Modifier = Modifier,
    thumbModifier: Modifier = Modifier,
    viewModel: AudioEffectsViewModel = koinViewModel(),
) {
    val colors = LocalAppColors.current

    Slider(
        value = presentLvlsDbState[index],
        valueRange = minDb..maxDb,
        colors = SliderDefaults.colors(activeTrackColor = colors.primary),
        modifier = modifier.sliderPositionProvider(
            sliderYPosState = sliderYPosState,
            sliderWidthState = sliderWidthState,
            sliderHeightState = sliderHeightState
        ),
        onValueChange = { level ->
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                val bands = updatedEQBandLevels(
                    level = level,
                    index = index,
                    presentLvlsDbState = presentLvlsDbState,
                    equalizerData = equalizerData!!,
                )

                viewModel.updateEqualizerBands(bands)
                viewModel.updateEqualizerParam(EqualizerBandsPreset.CUSTOM)
            }
        },
        thumb = {
            EqualizerThumb(
                index = index,
                pointsState = pointsState,
                sliderWidth = sliderWidthState.intValue,
                sliderYPos = sliderYPosState.floatValue,
                modifier = thumbModifier
            )
        },
    )
}

@Composable
private fun EqualizerThumb(
    index: Int,
    pointsState: SnapshotStateList<Offset>,
    sliderWidth: Int,
    sliderYPos: Float,
    modifier: Modifier = Modifier
) = Image(
    painter = painterResource(R.drawable.audio_band_button),
    contentDescription = stringResource(R.string.equalizer_band),
    contentScale = ContentScale.FillHeight,
    modifier = modifier
        .height(20.dp)
        .onGloballyPositioned {
            pointsState[index] = it
                .positionInWindow()
                .let { offset ->
                    offset.copy(
                        y = offset.y + sliderWidth / 2
                                - sliderYPos - it.size.width / 2
                    )
                }
        }
)

private fun Modifier.sliderPositionProvider(
    sliderYPosState: MutableFloatState,
    sliderWidthState: MutableIntState,
    sliderHeightState: MutableIntState,
) = this.onGloballyPositioned { coords ->
    sliderYPosState.floatValue = coords.positionInWindow().y
    sliderWidthState.intValue = coords.size.width
    sliderHeightState.intValue = coords.size.height
}