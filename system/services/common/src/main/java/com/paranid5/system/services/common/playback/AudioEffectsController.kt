package com.paranid5.system.services.common.playback

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.PresetReverb
import com.paranid5.crescendo.core.impl.di.EQUALIZER_DATA
import com.paranid5.crescendo.core.media.extensions.fromEqualizer
import com.paranid5.crescendo.core.media.extensions.usePreset
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerBandsPreset
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

interface AudioEffectsController {
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

class AudioEffectsControllerImpl : AudioEffectsController, KoinComponent {
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
        parameter: EqualizerBandsPreset,
    ) = equalizerDataState.update {
        EqualizerData.fromEqualizer(
            eq = equalizer,
            bandLevels = bandLevels,
            currentPreset = preset,
            currentParameter = parameter,
        )
    }

    override fun releaseAudioEffects() {
        equalizer.release()
        bassBoost.release()
        reverb.release()
        equalizerDataState.update { null }
    }
}
