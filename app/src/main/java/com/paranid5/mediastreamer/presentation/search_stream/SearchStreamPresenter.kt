package com.paranid5.mediastreamer.presentation.search_stream

import com.paranid5.mediastreamer.presentation.BasePresenter
import kotlinx.coroutines.flow.MutableStateFlow

class SearchStreamPresenter(currentText: String?) : BasePresenter {
    val currentTextState = MutableStateFlow(currentText)
}