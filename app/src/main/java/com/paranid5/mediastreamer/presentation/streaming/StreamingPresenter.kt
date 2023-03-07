package com.paranid5.mediastreamer.presentation.streaming

import com.paranid5.mediastreamer.presentation.BasePresenter
import kotlinx.coroutines.flow.MutableStateFlow

class StreamingPresenter(isPlaying: Boolean) : BasePresenter {
    val isPlaying = MutableStateFlow(isPlaying)
}