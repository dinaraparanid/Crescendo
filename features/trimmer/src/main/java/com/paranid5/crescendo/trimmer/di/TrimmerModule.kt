package com.paranid5.crescendo.trimmer.di

import com.paranid5.crescendo.trimmer.domain.TrimmerInteractor
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val trimmerModule = module {
    singleOf(::TrimmerInteractor)
    viewModelOf(::TrimmerViewModel)
}