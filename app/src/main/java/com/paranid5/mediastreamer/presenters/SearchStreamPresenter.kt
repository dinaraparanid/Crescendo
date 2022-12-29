package com.paranid5.mediastreamer.presenters

import kotlinx.coroutines.flow.MutableStateFlow

class SearchStreamPresenter(currentText: String?) : BasePresenter {
    val currentTextState = MutableStateFlow(currentText)
}