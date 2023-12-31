package com.paranid5.crescendo.services.stream_service.playback.effects

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.PresetReverb
import com.paranid5.crescendo.EQUALIZER_DATA
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.states.effects.AudioEffectsEnabledStateSubscriber
import com.paranid5.crescendo.data.states.effects.AudioEffectsEnabledStateSubscriberImpl
import com.paranid5.crescendo.data.states.effects.BassStrengthStateSubscriber
import com.paranid5.crescendo.data.states.effects.BassStrengthStateSubscriberImpl
import com.paranid5.crescendo.data.states.effects.EqualizerBandsStateSubscriber
import com.paranid5.crescendo.data.states.effects.EqualizerBandsStateSubscriberImpl
import com.paranid5.crescendo.data.states.effects.EqualizerParamStateSubscriber
import com.paranid5.crescendo.data.states.effects.EqualizerParamStateSubscriberImpl
import com.paranid5.crescendo.data.states.effects.EqualizerPresetStateSubscriber
import com.paranid5.crescendo.data.states.effects.EqualizerPresetStateSubscriberImpl
import com.paranid5.crescendo.data.states.effects.PitchStateSubscriber
import com.paranid5.crescendo.data.states.effects.PitchStateSubscriberImpl
import com.paranid5.crescendo.data.states.effects.ReverbPresetStateSubscriber
import com.paranid5.crescendo.data.states.effects.ReverbPresetStateSubscriberImpl
import com.paranid5.crescendo.data.states.effects.SpeedStateSubscriber
import com.paranid5.crescendo.data.states.effects.SpeedStateSubscriberImpl
import com.paranid5.crescendo.domain.eq.EqualizerBandsPreset
import com.paranid5.crescendo.domain.eq.EqualizerData
import com.paranid5.crescendo.domain.utils.extensions.usePreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class AudioEffectsController(storageHandler: StorageHandler) : KoinComponent,
    AudioEffectsEnabledStateSubscriber by AudioEffectsEnabledStateSubscriberImpl(storageHandler),
    PitchStateSubscriber by PitchStateSubscriberImpl(storageHandler),
    SpeedStateSubscriber by SpeedStateSubscriberImpl(storageHandler),
    EqualizerParamStateSubscriber by EqualizerParamStateSubscriberImpl(storageHandler),
    EqualizerBandsStateSubscriber by EqualizerBandsStateSubscriberImpl(storageHandler),
    EqualizerPresetStateSubscriber by EqualizerPresetStateSubscriberImpl(storageHandler),
    BassStrengthStateSubscriber by BassStrengthStateSubscriberImpl(storageHandler),
    ReverbPresetStateSubscriber by ReverbPresetStateSubscriberImpl(storageHandler) {
    private val equalizerDataState by inject<MutableStateFlow<EqualizerData?>>(
        named(EQUALIZER_DATA)
    )

    lateinit var equalizer: Equalizer
        private set

    lateinit var bassBoost: BassBoost
        private set

    lateinit var reverb: PresetReverb
        private set

    fun initAudioEffects(audioSessionId: Int) {
        equalizer = Equalizer(0, audioSessionId)
        bassBoost = BassBoost(0, audioSessionId)
        reverb = PresetReverb(0, audioSessionId)
    }

    fun setEqParameter(
        bandLevels: List<Short>?,
        preset: Short,
        currentParameter: EqualizerBandsPreset,
    ) {
        equalizer.usePreset(currentParameter, bandLevels, preset)
        updateEqData(bandLevels, preset, currentParameter)
    }

    fun setBassStrength(bassStrength: Short) =
        bassBoost.setStrength(bassStrength)

    fun setReverbPreset(reverbPreset: Short) {
        reverb.preset = reverbPreset
    }

    private fun updateEqData(
        bandLevels: List<Short>?,
        preset: Short,
        parameter: EqualizerBandsPreset
    ) = equalizerDataState.update {
        EqualizerData(equalizer, bandLevels, preset, parameter)
    }

    fun releaseAudioEffects() {
        equalizer.release()
        bassBoost.release()
        reverb.release()
        equalizerDataState.update { null }
    }
}