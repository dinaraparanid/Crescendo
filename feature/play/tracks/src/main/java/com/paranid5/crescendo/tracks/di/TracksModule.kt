package com.paranid5.crescendo.tracks.di

import com.paranid5.crescendo.tracks.view_model.TracksViewModelImpl
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val tracksModule = module {
    viewModelOf(::TracksViewModelImpl)
}