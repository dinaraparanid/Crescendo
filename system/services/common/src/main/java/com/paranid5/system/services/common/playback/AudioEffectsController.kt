package com.paranid5.system.services.common.playback

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.PresetReverb
import com.paranid5.crescendo.core.media.extensions.fromEqualizer
import com.paranid5.crescendo.core.media.extensions.usePreset
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsRepository
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerBandsPreset
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerData

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

class AudioEffectsControllerImpl(
    private val audioEffectsRepository: AudioEffectsRepository,
) : AudioEffectsController {
    override lateinit var equalizer: Equalizer
        private set

    override lateinit var bassBoost: BassBoost
        private set

    override lateinit var reverb: PresetReverb
        private set

    override fun initAudioEffects(audioSessionId: Int) {
        equalizer = Equalizer(0, audioSessionId)
        bassBoost = BassBoost(0, audioSessionId)
        reverb = PresetReverb(0, audioSessionId) // Это не правильно, оставил, чтобы не искажал звук
        // reverb = PresetReverb(0, 0) <- правильно https://stackoverflow.com/questions/61775727/using-android-presetreverb-on-audiotrack
    }

    override fun setEqParameter(
        bandLevels: List<Short>?,
        preset: Short,
        currentParameter: EqualizerBandsPreset,
    ) {
        equalizer.usePreset(currentParameter, bandLevels, preset)
        updateEqData(bandLevels, preset, currentParameter)
    }

    override fun setBassStrength(bassStrength: Short) {
        kotlin.runCatching { bassBoost.setStrength(bassStrength) }
    }

    override fun setReverbPreset(reverbPreset: Short) {
        kotlin.runCatching { reverb.preset = reverbPreset }.onFailure {
            it.printStackTrace()
        }
    }

    private fun updateEqData(
        bandLevels: List<Short>?,
        preset: Short,
        parameter: EqualizerBandsPreset,
    ) = audioEffectsRepository.updateEqualizerData(
        EqualizerData.fromEqualizer(
            eq = equalizer,
            bandLevels = bandLevels,
            currentPreset = preset,
            currentParameter = parameter,
        )
    )

    override fun releaseAudioEffects() {
        equalizer.release()
        bassBoost.release()
        reverb.release()
        audioEffectsRepository.updateEqualizerData(equalizerData = null)
    }
}
