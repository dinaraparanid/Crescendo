package com.paranid5.mediastreamer.presentation

import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinComponent

abstract class ObservableViewModel<P : BasePresenter, H : UIHandler> : ViewModel(), KoinComponent {
    abstract val presenter: P
    abstract val handler: H
}