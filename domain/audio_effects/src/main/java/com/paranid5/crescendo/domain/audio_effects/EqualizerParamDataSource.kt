package com.paranid5.crescendo.domain.audio_effects

import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerBandsPreset
import kotlinx.coroutines.flow.Flow

interface EqualizerParamDataSource {
    val equalizerParamFlow: Flow<EqualizerBandsPreset>

    suspend fun setEqualizerParam(param: EqualizerBandsPreset)
}
