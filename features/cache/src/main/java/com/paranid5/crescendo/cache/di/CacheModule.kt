package com.paranid5.crescendo.cache.di

import com.paranid5.crescendo.cache.domain.CacheInteractor
import com.paranid5.crescendo.cache.presentation.CacheViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val cacheModule = module {
    singleOf(::CacheInteractor)
    viewModelOf(::CacheViewModel)
}