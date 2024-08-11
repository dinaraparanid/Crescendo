package com.paranid5.crescendo.audio_effects.presentation.ui.entity

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerBandsPreset
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class EqualizerUiState(
    val minBandLevel: Short,
    val maxBandLevel: Short,
    val bandLevels: ImmutableList<Short>,
    val presets: ImmutableList<String>,
    val currentPreset: Short,
    val bandFrequencies: ImmutableList<Int>,
    val bandsPreset: EqualizerBandsPreset,
) : Parcelable {
    companion object {
        fun fromDTO(data: EqualizerData) = EqualizerUiState(
            minBandLevel = data.minBandLevel,
            maxBandLevel = data.maxBandLevel,
            bandLevels = data.bandLevels.toImmutableList(),
            presets = data.presets.toImmutableList(),
            currentPreset = data.currentPreset,
            bandFrequencies = data.bandFrequencies.toImmutableList(),
            bandsPreset = data.bandsPreset,
        )
    }
}
