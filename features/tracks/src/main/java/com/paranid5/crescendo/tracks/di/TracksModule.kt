package com.paranid5.crescendo.tracks.di

import com.paranid5.crescendo.tracks.TracksViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val tracksModule = module {
    viewModelOf(::TracksViewModel)
}