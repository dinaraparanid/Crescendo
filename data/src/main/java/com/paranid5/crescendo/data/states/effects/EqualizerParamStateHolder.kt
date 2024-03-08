package com.paranid5.crescendo.data.states.effects

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.equalizerParamFlow
import com.paranid5.crescendo.data.properties.storeEqualizerParam
import com.paranid5.crescendo.core.common.eq.EqualizerBandsPreset
import kotlinx.coroutines.flow.Flow

interface EqualizerParamStateSubscriber {
    val equalizerParamFlow: Flow<com.paranid5.crescendo.core.common.eq.EqualizerBandsPreset>
}

interface EqualizerParamStatePublisher {
    suspend fun setEqualizerParam(param: com.paranid5.crescendo.core.common.eq.EqualizerBandsPreset)
}

class EqualizerParamStateSubscriberImpl(private val storageHandler: StorageHandler) :
    EqualizerParamStateSubscriber {
    override val equalizerParamFlow by lazy {
        storageHandler.equalizerParamFlow
    }
}

class EqualizerParamStatePublisherImpl(private val storageHandler: StorageHandler) :
    EqualizerParamStatePublisher {
    override suspend fun setEqualizerParam(param: com.paranid5.crescendo.core.common.eq.EqualizerBandsPreset) =
        storageHandler.storeEqualizerParam(param)
}