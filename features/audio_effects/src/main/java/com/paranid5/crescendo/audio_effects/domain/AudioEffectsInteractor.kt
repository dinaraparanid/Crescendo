package com.paranid5.crescendo.audio_effects.domain

import com.paranid5.crescendo.core.common.eq.EqualizerData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.core.component.KoinComponent

internal fun isParamInputValid(input: String): Boolean {
    val value = input.toFloatOrNull() ?: return false
    return value in 0.25F..2F
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