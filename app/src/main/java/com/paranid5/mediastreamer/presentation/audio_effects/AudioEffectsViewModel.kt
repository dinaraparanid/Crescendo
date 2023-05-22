package com.paranid5.mediastreamer.presentation.audio_effects

import com.paranid5.mediastreamer.presentation.ObservableViewModel
import org.koin.core.component.inject

class AudioEffectsViewModel : ObservableViewModel<AudioEffectsPresenter, AudioEffectsUIHandler>() {
    override val presenter by inject<AudioEffectsPresenter>()
    override val handler by inject<AudioEffectsUIHandler>()
}