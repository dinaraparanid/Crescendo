package com.paranid5.crescendo.cache.di

import com.paranid5.crescendo.cache.view_model.CacheViewModelImpl
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val cacheModule = module {
    viewModelOf(::CacheViewModelImpl)
}
