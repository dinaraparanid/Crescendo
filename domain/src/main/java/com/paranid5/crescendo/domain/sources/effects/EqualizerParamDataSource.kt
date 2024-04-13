package com.paranid5.crescendo.domain.sources.effects

import com.paranid5.crescendo.core.common.eq.EqualizerBandsPreset
import kotlinx.coroutines.flow.Flow

interface EqualizerParamSubscriber {
    val equalizerParamFlow: Flow<EqualizerBandsPreset>
}

interface EqualizerParamPublisher {
    suspend fun setEqualizerParam(param: EqualizerBandsPreset)
}