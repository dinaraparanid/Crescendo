package com.paranid5.mediastreamer.presentation.audio_effects

import android.content.Context
import com.paranid5.mediastreamer.data.eq.EqualizerParameters
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.domain.stream_service.StreamService
import com.paranid5.mediastreamer.domain.utils.extensions.sendBroadcast
import com.paranid5.mediastreamer.presentation.UIHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AudioEffectsUIHandler : UIHandler, KoinComponent, CoroutineScope by MainScope() {
    private val storageHandler by inject<StorageHandler>()

    fun storeAudioEffectsEnabledAsync(isEnabled: Boolean) = launch(Dispatchers.IO) {
        storageHandler.storeAudioEffectsEnabled(isEnabled)
    }

    fun isParamInputValid(input: String) =
        input.takeIf { s -> s.toFloatOrNull()?.takeIf { it in 0.25F..2F } != null } != null

    fun storePitchAsync(pitch: Float) = launch(Dispatchers.IO) {
        storageHandler.storePitch(pitch)
    }

    fun storeSpeedAsync(speed: Float) = launch(Dispatchers.IO) {
        storageHandler.storeSpeed(speed)
    }

    private fun sendEqualizerParamUpdate(context: Context) =
        context.sendBroadcast(StreamService.Broadcast_EQUALIZER_PARAM_UPDATE)

    fun storeEQPresetAsync(context: Context, preset: Short) = launch(Dispatchers.IO) {
        storageHandler.storeEqualizerPreset(preset)
        storageHandler.storeEqualizerParam(EqualizerParameters.PRESET)
        sendEqualizerParamUpdate(context)
    }

    fun switchToBandsAsync(context: Context) = launch(Dispatchers.IO) {
        storageHandler.storeEqualizerParam(EqualizerParameters.BANDS)
        sendEqualizerParamUpdate(context)
    }

    fun storeEQBandsAsync(context: Context, bandLevels: List<Short>) = launch(Dispatchers.IO) {
        storageHandler.storeEqualizerBands(bandLevels)
        switchToBandsAsync(context).join()
    }
}