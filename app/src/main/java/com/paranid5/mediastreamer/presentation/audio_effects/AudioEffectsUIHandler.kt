package com.paranid5.mediastreamer.presentation.audio_effects

import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.presentation.UIHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AudioEffectsUIHandler : UIHandler, KoinComponent, CoroutineScope by MainScope() {
    private val storageHandler by inject<StorageHandler>()

    fun storeAudioEffectsEnabledAsync(isEnabled: Boolean) = launch {
        storageHandler.storeAudioEffectsEnabled(isEnabled)
    }
}