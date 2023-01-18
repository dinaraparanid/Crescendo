package com.paranid5.mediastreamer.presentation.view_models

import androidx.lifecycle.ViewModel
import com.paranid5.mediastreamer.presentation.presenters.BasePresenter
import com.paranid5.mediastreamer.presentation.ui_handlers.UIHandler
import org.koin.core.component.KoinComponent

abstract class ObservableViewModel<P : BasePresenter, H : UIHandler> : ViewModel(), KoinComponent {
    abstract val presenter: P
    abstract val handler: H
}