package com.paranid5.crescendo.audio_effects.view_model

sealed interface AudioEffectsUiIntent {

    sealed interface Lifecycle : AudioEffectsUiIntent {
        data object OnStart : Lifecycle
        data object OnStop : Lifecycle
    }

    sealed interface UpdateData : AudioEffectsUiIntent {
        data class UpdateAudioEffectsEnabled(val enabled: Boolean) : UpdateData
        data class UpdatePitch(val pitch: Float) : UpdateData
        data class UpdateSpeed(val speed: Float) : UpdateData
        data class UpdateEqPreset(val presetIndex: Int) : UpdateData
        data class UpdateEqBandLevels(val level: Float, val index: Int) : UpdateData
        data class UpdateBassStrength(val strength: Short) : UpdateData
        data class UpdateReverbPreset(val preset: Short) : UpdateData
    }
}
