package com.paranid5.crescendo.audio_effects.view_model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.audio_effects.presentation.ui.entity.EqualizerUiState
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerBandsPreset
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.utils.extensions.orNil
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class AudioEffectsState(
    val playbackStatus: PlaybackStatus? = null,
    val areAudioEffectsEnabled: Boolean = false,
    val bassStrength: Short = 0,
    val reverbPreset: Short = 0,
    val pitchText: String = "",
    val speedText: String = "",
    val equalizerUiState: EqualizerUiState? = null,
    val customPresetIndex: Int = equalizerUiState?.presets?.size ?: 0,
    val selectedPresetIndex: Int = when (equalizerUiState?.bandsPreset) {
        EqualizerBandsPreset.CUSTOM -> customPresetIndex
        EqualizerBandsPreset.BUILT_IN -> equalizerUiState.currentPreset.toInt()
        EqualizerBandsPreset.NIL -> 0
        null -> 0
    },
    val uiState: UiState<Unit> = UiState.Initial,
) : Parcelable {
    @IgnoredOnParcel
    val isCustomPresetActive = equalizerUiState?.bandsPreset == EqualizerBandsPreset.CUSTOM

    @IgnoredOnParcel
    val currentPresetIndex = when {
        isCustomPresetActive -> customPresetIndex
        else -> selectedPresetIndex
    }

    @IgnoredOnParcel
    val builtInPresets = equalizerUiState?.presets.orNil()

    @IgnoredOnParcel
    val bandsAmount = equalizerUiState?.bandLevels?.size ?: 0
}
