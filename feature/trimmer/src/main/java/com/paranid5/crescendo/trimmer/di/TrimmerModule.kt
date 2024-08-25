package com.paranid5.crescendo.trimmer.di

import com.paranid5.crescendo.trimmer.view_model.TrimmerViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val trimmerModule = module {
    viewModelOf(::TrimmerViewModelImpl)
}