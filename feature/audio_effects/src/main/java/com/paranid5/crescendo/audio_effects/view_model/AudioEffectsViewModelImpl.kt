package com.paranid5.crescendo.audio_effects.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.raise.nullable
import com.paranid5.crescendo.audio_effects.domain.updatedEQBandLevels
import com.paranid5.crescendo.audio_effects.presentation.ui.entity.EqualizerUiState
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsUiIntent.Lifecycle
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsUiIntent.UpdateData
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsRepository
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerBandsPreset
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerData
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.utils.extensions.sideEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class AudioEffectsViewModelImpl(
    private val savedStateHandle: SavedStateHandle,
    private val audioEffectsRepository: AudioEffectsRepository,
    private val playbackRepository: PlaybackRepository,
) : ViewModel(), AudioEffectsViewModel, StatePublisher<AudioEffectsState> {
    companion object {
        private const val StateKey = "state"
    }

    private var dataUpdatesJob: Job? = null

    override val stateFlow = savedStateHandle.getStateFlow(StateKey, AudioEffectsState())

    override fun updateState(func: AudioEffectsState.() -> AudioEffectsState) {
        savedStateHandle[StateKey] = func(state)
    }

    override fun onUiIntent(intent: AudioEffectsUiIntent) = when (intent) {
        is Lifecycle -> onLifecycleUiIntent(intent)
        is UpdateData -> onUpdateStateUiIntent(intent)
    }

    private fun onLifecycleUiIntent(intent: Lifecycle) = when (intent) {
        is Lifecycle.OnStart -> subscribeOnDataUpdates()
        is Lifecycle.OnStop -> unsubscribeFromDataUpdates()
    }

    private fun onUpdateStateUiIntent(intent: UpdateData) = when (intent) {
        is UpdateData.UpdateAudioEffectsEnabled -> viewModelScope.sideEffect {
            audioEffectsRepository.updateAudioEffectsEnabled(areAudioEffectsEnabled = intent.enabled)
        }

        is UpdateData.UpdatePitch -> viewModelScope.sideEffect {
            audioEffectsRepository.updatePitch(pitch = intent.pitch)
        }

        is UpdateData.UpdateSpeed -> viewModelScope.sideEffect {
            audioEffectsRepository.updateSpeed(speed = intent.speed)
        }

        is UpdateData.UpdateEqPreset -> updateEqPreset(presetIndex = intent.presetIndex)

        is UpdateData.UpdateEqBandLevels -> updateEqBandLevels(
            level = intent.level,
            index = intent.index,
        )

        is UpdateData.UpdateBassStrength -> viewModelScope.sideEffect {
            audioEffectsRepository.updateBassStrength(bassStrength = intent.strength)
        }

        is UpdateData.UpdateReverbPreset -> viewModelScope.sideEffect {
            audioEffectsRepository.updateReverbPreset(reverbPreset = intent.preset)
        }
    }

    private fun subscribeOnDataUpdates() {
        dataUpdatesJob = viewModelScope.launch(Dispatchers.Default) {
            combine(
                playbackRepository.playbackStatusFlow,
                audioEffectsRepository.areAudioEffectsEnabledFlow,
                audioEffectsRepository.bassStrengthFlow,
                audioEffectsRepository.reverbPresetFlow,
                audioEffectsRepository.pitchTextFlow,
                audioEffectsRepository.speedTextFlow,
                audioEffectsRepository.equalizerState,
            ) { params ->
                AudioEffectsState(
                    playbackStatus = params[0] as PlaybackStatus?,
                    areAudioEffectsEnabled = params[1] as Boolean,
                    bassStrength = params[2] as Short,
                    reverbPreset = params[3] as Short,
                    pitchText = params[4] as String,
                    speedText = params[5] as String,
                    equalizerUiState = (params[6] as EqualizerData?)?.let(EqualizerUiState::fromDTO),
                    uiState = UiState.Success,
                )
            }.distinctUntilChanged().collectLatest {
                updateState { it }
            }
        }
    }

    private fun unsubscribeFromDataUpdates() {
        dataUpdatesJob?.cancel()
        dataUpdatesJob = null
    }

    private fun updateEqPreset(presetIndex: Int) {
        updateState { copy(selectedPresetIndex = presetIndex) }

        viewModelScope.launch {
            audioEffectsRepository.run {
                when (presetIndex) {
                    state.currentPresetIndex -> updateEqualizerParam(EqualizerBandsPreset.CUSTOM)

                    else -> {
                        updateEqualizerParam(EqualizerBandsPreset.BUILT_IN)
                        updateEqualizerPreset(presetIndex.toShort())
                    }
                }
            }
        }
    }

    private fun updateEqBandLevels(level: Float, index: Int) = viewModelScope.sideEffect {
        nullable {
            val bands = withContext(Dispatchers.Default) {
                updatedEQBandLevels(
                    level = level,
                    index = index,
                    equalizerUiState = state.equalizerUiState.bind(),
                )
            }

            viewModelScope.launch(Dispatchers.IO) {
                audioEffectsRepository.updateEqualizerBands(bands)
                audioEffectsRepository.updateEqualizerParam(EqualizerBandsPreset.CUSTOM)
            }
        }
    }
}
