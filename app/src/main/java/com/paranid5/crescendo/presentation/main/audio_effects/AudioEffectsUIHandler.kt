package com.paranid5.crescendo.presentation.main.audio_effects

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.storeAudioEffectsEnabled
import com.paranid5.crescendo.data.properties.storeBassStrength
import com.paranid5.crescendo.data.properties.storeEqualizerBands
import com.paranid5.crescendo.data.properties.storeEqualizerParam
import com.paranid5.crescendo.data.properties.storeEqualizerPreset
import com.paranid5.crescendo.data.properties.storeReverbPreset
import com.paranid5.crescendo.domain.eq.EqualizerBandsPreset
import com.paranid5.crescendo.domain.eq.EqualizerData
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.domain.utils.extensions.sendBroadcast
import com.paranid5.crescendo.presentation.UIHandler
import com.paranid5.crescendo.services.stream_service.StreamService
import com.paranid5.crescendo.services.track_service.TrackService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AudioEffectsUIHandler : UIHandler, KoinComponent {
    private val storageHandler by inject<StorageHandler>()

    fun isParamInputValid(input: String): Boolean {
        val value = input.toFloatOrNull() ?: return false
        return value in 0.25F..2F
    }

    private fun Context.sendAudioEffectsBroadcast(
        streamBroadcastAction: String,
        trackBroadcastAction: String,
        audioStatus: AudioStatus
    ) = audioStatus.handle(
        streamAction = { sendBroadcast(streamBroadcastAction) },
        trackAction = { sendBroadcast(trackBroadcastAction) }
    )

    suspend fun storeAudioEffectsEnabled(
        context: Context,
        isEnabled: Boolean,
        audioStatus: AudioStatus
    ) {
        storageHandler.storeAudioEffectsEnabled(isEnabled)

        context.sendAudioEffectsBroadcast(
            streamBroadcastAction = StreamService.Broadcast_AUDIO_EFFECTS_ENABLED_UPDATE,
            trackBroadcastAction = TrackService.Broadcast_AUDIO_EFFECTS_ENABLED_UPDATE,
            audioStatus = audioStatus
        )
    }

    private fun sendEqualizerParamUpdate(context: Context, audioStatus: AudioStatus) =
        context.sendAudioEffectsBroadcast(
            streamBroadcastAction = StreamService.Broadcast_EQUALIZER_PARAM_UPDATE,
            trackBroadcastAction = TrackService.Broadcast_EQUALIZER_PARAM_UPDATE,
            audioStatus = audioStatus
        )

    suspend fun storeAndSwitchToPreset(
        context: Context,
        preset: Short,
        audioStatus: AudioStatus
    ) {
        storageHandler.storeEqualizerPreset(preset)
        storageHandler.storeEqualizerParam(EqualizerBandsPreset.BUILT_IN)
        sendEqualizerParamUpdate(context, audioStatus)
    }

    suspend fun switchToBands(context: Context, audioStatus: AudioStatus) {
        storageHandler.storeEqualizerParam(EqualizerBandsPreset.CUSTOM)
        sendEqualizerParamUpdate(context, audioStatus)
    }

    suspend fun storeAndSwitchToBands(
        context: Context,
        bandLevels: List<Short>,
        audioStatus: AudioStatus
    ) {
        storageHandler.storeEqualizerBands(bandLevels)
        switchToBands(context, audioStatus)
    }

    suspend fun storeAndSendBassStrength(
        context: Context,
        bassStrength: Short,
        audioStatus: AudioStatus
    ) {
        storageHandler.storeBassStrength(bassStrength)

        context.sendAudioEffectsBroadcast(
            streamBroadcastAction = StreamService.Broadcast_BASS_STRENGTH_UPDATE,
            trackBroadcastAction = TrackService.Broadcast_BASS_STRENGTH_UPDATE,
            audioStatus = audioStatus
        )
    }

    suspend fun storeAndSendReverbPresetAsync(
        context: Context,
        reverbPreset: Short,
        audioStatus: AudioStatus
    ) {
        storageHandler.storeReverbPreset(reverbPreset)

        context.sendAudioEffectsBroadcast(
            streamBroadcastAction = StreamService.Broadcast_REVERB_PRESET_UPDATE,
            trackBroadcastAction = TrackService.Broadcast_REVERB_PRESET_UPDATE,
            audioStatus = audioStatus
        )
    }

    suspend fun updateEQBandLevel(
        level: Float,
        index: Int,
        presentLvlsDbState: MutableList<Float>,
        equalizerData: EqualizerData,
        context: Context,
        audioStatus: AudioStatus
    ) {
        equalizerData.bandLevels.forEachIndexed { ind, mdb ->
            presentLvlsDbState[ind] = mdb / 1000F
        }

        presentLvlsDbState[index] = level

        val newLevels = equalizerData.bandLevels.toMutableList().also {
            it[index] = (level * 1000).toInt().toShort()
        }

        storeAndSwitchToBands(
            context = context,
            bandLevels = newLevels,
            audioStatus = audioStatus
        )
    }
}