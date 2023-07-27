package com.paranid5.mediastreamer.presentation.fetch_stream

import com.paranid5.mediastreamer.presentation.BasePresenter
import kotlinx.coroutines.flow.MutableStateFlow

class FetchStreamPresenter(currentText: String?) : BasePresenter {
    val currentTextState = MutableStateFlow(currentText)
}