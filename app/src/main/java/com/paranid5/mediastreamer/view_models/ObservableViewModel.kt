package com.paranid5.mediastreamer.view_models

import androidx.lifecycle.ViewModel
import com.paranid5.mediastreamer.presenters.BasePresenter
import com.paranid5.mediastreamer.ui_handlers.UIHandler
import org.koin.core.component.KoinComponent

abstract class ObservableViewModel<P : BasePresenter, H : UIHandler> : ViewModel(), KoinComponent {
    abstract val presenter: P
    abstract val handler: H
}