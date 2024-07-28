package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.domain.audio_effects.EqualizerStateHolder
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class EqualizerStateHolderImpl : EqualizerStateHolder {
    private val _equalizerState = MutableStateFlow<EqualizerData?>(null)

    override val equalizerState = _equalizerState.asStateFlow()

    override fun updateEqualizerData(equalizerData: EqualizerData?) =
        _equalizerState.update { equalizerData }
}
