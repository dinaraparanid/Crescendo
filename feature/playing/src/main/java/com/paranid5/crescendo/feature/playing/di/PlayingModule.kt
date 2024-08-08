package com.paranid5.crescendo.feature.playing.di

import com.paranid5.crescendo.feature.playing.domain.PlayingInteractor
import com.paranid5.crescendo.feature.playing.view_model.PlayingViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val playingModule = module {
    singleOf(::PlayingInteractor)
    viewModelOf(::PlayingViewModelImpl)
}