package com.paranid5.system.services.common.playback

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.PresetReverb
import com.paranid5.crescendo.core.common.eq.EqualizerBandsPreset
import com.paranid5.crescendo.core.common.eq.EqualizerData
import com.paranid5.crescendo.core.impl.di.EQUALIZER_DATA
import com.paranid5.crescendo.core.media.eq.EqualizerData.fromEqualizer
import com.paranid5.crescendo.core.media.eq.usePreset
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.effects.AudioEffectsEnabledStateSubscriber
import com.paranid5.crescendo.data.sources.effects.AudioEffectsEnabledStateSubscriberImpl
import com.paranid5.crescendo.data.sources.effects.BassStrengthStateSubscriber
import com.paranid5.crescendo.data.sources.effects.BassStrengthStateSubscriberImpl
import com.paranid5.crescendo.data.sources.effects.EqualizerBandsStateSubscriber
import com.paranid5.crescendo.data.sources.effects.EqualizerBandsStateSubscriberImpl
import com.paranid5.crescendo.data.sources.effects.EqualizerParamStateSubscriber
import com.paranid5.crescendo.data.sources.effects.EqualizerParamStateSubscriberImpl
import com.paranid5.crescendo.data.sources.effects.EqualizerPresetStateSubscriber
import com.paranid5.crescendo.data.sources.effects.EqualizerPresetStateSubscriberImpl
import com.paranid5.crescendo.data.sources.effects.PitchStateSubscriber
import com.paranid5.crescendo.data.sources.effects.PitchStateSubscriberImpl
import com.paranid5.crescendo.data.sources.effects.ReverbPresetStateSubscriber
import com.paranid5.crescendo.data.sources.effects.ReverbPresetStateSubscriberImpl
import com.paranid5.crescendo.data.sources.effects.SpeedStateSubscriber
import com.paranid5.crescendo.data.sources.effects.SpeedStateSubscriberImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

interface AudioEffectsController :
    AudioEffectsEnabledStateSubscriber,
    PitchStateSubscriber,
    SpeedStateSubscriber,
    EqualizerParamStateSubscriber,
    EqualizerBandsStateSubscriber,
    EqualizerPresetStateSubscriber,
    BassStrengthStateSubscriber,
    ReverbPresetStateSubscriber {
    val equalizer: Equalizer
    val bassBoost: BassBoost
    val reverb: PresetReverb

    fun initAudioEffects(audioSessionId: Int)

    fun setEqParameter(
        bandLevels: List<Short>?,
        preset: Short,
        currentParameter: EqualizerBandsPreset,
    )

    fun setBassStrength(bassStrength: Short)

    fun setReverbPreset(reverbPreset: Short)

    fun releaseAudioEffects()
}

class AudioEffectsControllerImpl(storageRepository: StorageRepository) :
    AudioEffectsController, KoinComponent,
    AudioEffectsEnabledStateSubscriber by AudioEffectsEnabledStateSubscriberImpl(storageRepository),
    PitchStateSubscriber by PitchStateSubscriberImpl(storageRepository),
    SpeedStateSubscriber by SpeedStateSubscriberImpl(storageRepository),
    EqualizerParamStateSubscriber by EqualizerParamStateSubscriberImpl(storageRepository),
    EqualizerBandsStateSubscriber by EqualizerBandsStateSubscriberImpl(storageRepository),
    EqualizerPresetStateSubscriber by EqualizerPresetStateSubscriberImpl(storageRepository),
    BassStrengthStateSubscriber by BassStrengthStateSubscriberImpl(storageRepository),
    ReverbPresetStateSubscriber by ReverbPresetStateSubscriberImpl(storageRepository) {
    private val equalizerDataState by inject<MutableStateFlow<EqualizerData?>>(
        named(EQUALIZER_DATA)
    )

    override lateinit var equalizer: Equalizer
        private set

    override lateinit var bassBoost: BassBoost
        private set

    override lateinit var reverb: PresetReverb
        private set

    override fun initAudioEffects(audioSessionId: Int) {
        equalizer = Equalizer(0, audioSessionId)
        bassBoost = BassBoost(0, audioSessionId)
        reverb = PresetReverb(0, audioSessionId)
    }

    override fun setEqParameter(
        bandLevels: List<Short>?,
        preset: Short,
        currentParameter: EqualizerBandsPreset,
    ) {
        equalizer.usePreset(currentParameter, bandLevels, preset)
        updateEqData(bandLevels, preset, currentParameter)
    }

    override fun setBassStrength(bassStrength: Short) =
        bassBoost.setStrength(bassStrength)

    override fun setReverbPreset(reverbPreset: Short) {
        reverb.preset = reverbPreset
    }

    private fun updateEqData(
        bandLevels: List<Short>?,
        preset: Short,
        parameter: EqualizerBandsPreset
    ) = equalizerDataState.update {
        fromEqualizer(
            equalizer,
            bandLevels,
            preset,
            parameter
        )
    }

    override fun releaseAudioEffects() {
        equalizer.release()
        bassBoost.release()
        reverb.release()
        equalizerDataState.update { null }
    }
}