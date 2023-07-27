package com.paranid5.mediastreamer.presentation.playing

import com.paranid5.mediastreamer.presentation.BasePresenter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayingPresenter(isPlayingState: MutableStateFlow<Boolean>) : BasePresenter {
    val isPlayingState = isPlayingState.asStateFlow()
}