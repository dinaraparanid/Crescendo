package com.paranid5.crescendo.presentation.main.audio_effects

import com.paranid5.crescendo.domain.eq.EqualizerData
import com.paranid5.crescendo.presentation.UIHandler
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.core.component.KoinComponent

class AudioEffectsUIHandler : UIHandler, KoinComponent {
    fun isParamInputValid(input: String): Boolean {
        val value = input.toFloatOrNull() ?: return false
        return value in 0.25F..2F
    }

    fun updatedEQBandLevels(
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
}