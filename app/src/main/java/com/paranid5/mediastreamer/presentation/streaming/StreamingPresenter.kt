package com.paranid5.mediastreamer.presentation.streaming

import com.paranid5.mediastreamer.presentation.BasePresenter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StreamingPresenter(isPlayingState: MutableStateFlow<Boolean>) : BasePresenter {
    val isPlayingState = isPlayingState.asStateFlow()
}