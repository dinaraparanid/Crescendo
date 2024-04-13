package com.paranid5.crescendo.data.sources.effects

import com.paranid5.crescendo.core.common.eq.EqualizerBandsPreset
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.equalizerParamFlow
import com.paranid5.crescendo.data.properties.storeEqualizerParam
import com.paranid5.crescendo.domain.sources.effects.EqualizerParamPublisher
import com.paranid5.crescendo.domain.sources.effects.EqualizerParamSubscriber

class EqualizerParamSubscriberImpl(private val storageRepository: StorageRepository) :
    EqualizerParamSubscriber {
    override val equalizerParamFlow by lazy {
        storageRepository.equalizerParamFlow
    }
}

class EqualizerParamPublisherImpl(private val storageRepository: StorageRepository) :
    EqualizerParamPublisher {
    override suspend fun setEqualizerParam(param: EqualizerBandsPreset) =
        storageRepository.storeEqualizerParam(param)
}