package com.paranid5.mediastreamer.presentation.presenters

import kotlinx.coroutines.flow.MutableStateFlow

class SearchStreamPresenter(currentText: String?) : BasePresenter {
    val currentTextState = MutableStateFlow(currentText)
}