package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.pitchFlow
import com.paranid5.crescendo.data.properties.storePitch
import com.paranid5.crescendo.domain.audio_effects.PitchDataSource

internal class PitchDataSourceImpl(
    private val storageRepository: StorageRepository,
) : PitchDataSource {
    override val pitchFlow by lazy {
        storageRepository.pitchFlow
    }

    override suspend fun setPitch(pitch: Float) =
        storageRepository.storePitch(pitch)
}
