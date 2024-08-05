package com.paranid5.crescendo.playing.di

import com.paranid5.crescendo.playing.domain.PlayingInteractor
import com.paranid5.crescendo.playing.view_model.PlayingViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val playingModule = module {
    singleOf(::PlayingInteractor)
    viewModelOf(::PlayingViewModel)
}