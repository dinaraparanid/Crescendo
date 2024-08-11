package com.paranid5.crescendo.audio_effects.domain

import com.paranid5.crescendo.audio_effects.presentation.ui.entity.EqualizerUiState
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerData.Companion.MILLIBELS_IN_DECIBEL
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentList

private const val MIN_AUDIO_EFFECT_VALUE = 0.25F
private const val MAX_AUDIO_EFFECT_VALUE = 2F

internal fun isParamInputValid(input: String): Boolean {
    val value = input.toFloatOrNull() ?: return false
    return value in MIN_AUDIO_EFFECT_VALUE..MAX_AUDIO_EFFECT_VALUE
}

internal fun updatedEQBandLevels(
    level: Float,
    index: Int,
    equalizerUiState: EqualizerUiState,
) = equalizerUiState.bandLevels.toPersistentList().mutate {
    it[index] = (level * MILLIBELS_IN_DECIBEL).toInt().toShort()
}
