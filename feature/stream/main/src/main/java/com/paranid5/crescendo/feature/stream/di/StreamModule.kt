package com.paranid5.crescendo.feature.stream.di

import com.paranid5.crescendo.feature.stream.fetch.di.fetchStreamModule
import com.paranid5.crescendo.feature.stream.view_model.StreamViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val streamModule = module {
    includes(fetchStreamModule)
    viewModelOf(::StreamViewModelImpl)
}