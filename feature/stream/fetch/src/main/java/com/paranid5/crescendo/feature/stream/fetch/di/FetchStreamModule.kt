package com.paranid5.crescendo.feature.stream.fetch.di

import com.paranid5.crescendo.feature.stream.fetch.view_model.FetchStreamViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val fetchStreamModule = module {
    viewModelOf(::FetchStreamViewModelImpl)
}
