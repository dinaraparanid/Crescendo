package com.paranid5.mediastreamer.presentation.audio_effects

import com.paranid5.mediastreamer.presentation.BasePresenter
import kotlinx.coroutines.flow.MutableStateFlow

class AudioEffectsPresenter(pitchText: String?, speedText: String?) : BasePresenter {
    val pitchTextState = MutableStateFlow(pitchText)
    val speedTextState = MutableStateFlow(speedText)
}