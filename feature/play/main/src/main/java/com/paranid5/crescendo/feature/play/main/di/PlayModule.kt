package com.paranid5.crescendo.feature.play.main.di

import com.paranid5.crescendo.feature.play.main.view_model.PlayViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val playModule = module {
    viewModelOf(::PlayViewModelImpl)
}