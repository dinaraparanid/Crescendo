package com.paranid5.crescendo.audio_effects.presentation.ui.equalizer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsState
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsUiIntent
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.ui.utils.Spinner
import com.paranid5.crescendo.utils.extensions.plus
import kotlinx.collections.immutable.persistentListOf

private val IconSize = 24.dp

@Composable
internal fun PresetSpinner(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Box(modifier) {
    PresetSpinnerImpl(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterStart),
    )

    PresetSpinnerArrow(
        Modifier
            .align(Alignment.CenterEnd)
            .size(IconSize)
            .padding(end = dimensions.padding.small),
    )
}

@Composable
private fun PresetSpinnerImpl(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val customPreset = stringResource(R.string.audio_effects_custom_preset)

    val presets = remember(state.builtInPresets, customPreset) {
        state.builtInPresets + customPreset
    }

    val selectedItemIndices = remember(state.selectedPresetIndex) {
        persistentListOf(state.selectedPresetIndex)
    }

    Spinner(
        items = presets,
        selectedItemIndices = selectedItemIndices,
        modifier = modifier.clip(RoundedCornerShape(dimensions.corners.small)),
        onItemSelected = { ind, _ ->
            onUiIntent(AudioEffectsUiIntent.UpdateData.UpdateEqPreset(presetIndex = ind))
        },
    )
}

@Composable
private fun PresetSpinnerArrow(modifier: Modifier = Modifier) =
    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_down),
        contentDescription = stringResource(R.string.audio_effects_eq_presets),
        tint = colors.primary,
        modifier = modifier,
    )
