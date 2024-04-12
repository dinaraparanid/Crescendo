package com.paranid5.crescendo.fetch_stream.di

import com.paranid5.crescendo.fetch_stream.presentation.FetchStreamViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val fetchStreamModule = module {
    viewModelOf(::FetchStreamViewModel)
}