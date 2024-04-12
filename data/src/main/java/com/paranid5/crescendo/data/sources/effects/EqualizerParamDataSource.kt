package com.paranid5.crescendo.data.sources.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.equalizerParamFlow
import com.paranid5.crescendo.data.properties.storeEqualizerParam
import kotlinx.coroutines.flow.Flow

interface EqualizerParamStateSubscriber {
    val equalizerParamFlow: Flow<com.paranid5.crescendo.core.common.eq.EqualizerBandsPreset>
}

interface EqualizerParamStatePublisher {
    suspend fun setEqualizerParam(param: com.paranid5.crescendo.core.common.eq.EqualizerBandsPreset)
}

class EqualizerParamStateSubscriberImpl(private val storageRepository: StorageRepository) :
    EqualizerParamStateSubscriber {
    override val equalizerParamFlow by lazy {
        storageRepository.equalizerParamFlow
    }
}

class EqualizerParamStatePublisherImpl(private val storageRepository: StorageRepository) :
    EqualizerParamStatePublisher {
    override suspend fun setEqualizerParam(param: com.paranid5.crescendo.core.common.eq.EqualizerBandsPreset) =
        storageRepository.storeEqualizerParam(param)
}