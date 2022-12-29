package com.paranid5.mediastreamer

import com.paranid5.mediastreamer.presenters.SearchStreamPresenter
import com.paranid5.mediastreamer.ui_handlers.SearchStreamUIHandler
import com.paranid5.mediastreamer.view_models.SearchStreamViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::SearchStreamUIHandler)
    factory { (currentText: String?) -> SearchStreamPresenter(currentText) }
    viewModelOf(::SearchStreamViewModel)
}