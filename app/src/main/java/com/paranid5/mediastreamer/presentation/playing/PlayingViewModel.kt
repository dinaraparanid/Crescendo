package com.paranid5.mediastreamer.presentation.playing

import com.paranid5.mediastreamer.presentation.ObservableViewModel
import org.koin.core.component.inject

class PlayingViewModel : ObservableViewModel<PlayingPresenter, PlayingUIHandler>() {
    override val handler by inject<PlayingUIHandler>()
    override val presenter by inject<PlayingPresenter>()
}