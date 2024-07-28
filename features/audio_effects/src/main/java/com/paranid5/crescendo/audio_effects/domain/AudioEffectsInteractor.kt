package com.paranid5.crescendo.audio_effects.domain

import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

private const val MIN_AUDIO_EFFECT_VALUE = 0.25F
private const val MAX_AUDIO_EFFECT_VALUE = 2F

internal fun isParamInputValid(input: String): Boolean {
    val value = input.toFloatOrNull() ?: return false
    return value in MIN_AUDIO_EFFECT_VALUE..MAX_AUDIO_EFFECT_VALUE
}

internal fun updatedEQBandLevels(
    level: Float,
    index: Int,
    presentLvlsDbState: MutableList<Float>,
    equalizerData: EqualizerData,
): ImmutableList<Short> {
    presentLvlsDbState[index] = level

    val newLevels = equalizerData.bandLevels.toMutableList().also {
        it[index] = (level * 1000).toInt().toShort()
    }

    return newLevels.toImmutableList()
}