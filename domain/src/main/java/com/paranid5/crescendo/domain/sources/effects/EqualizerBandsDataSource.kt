package com.paranid5.crescendo.domain.sources.effects

import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface EqualizerBandsSubscriber {
    val equalizerBandsFlow: Flow<ImmutableList<Short>>
}

interface EqualizerBandsPublisher {
    suspend fun setEqualizerBands(bands: ImmutableList<Short>)
}