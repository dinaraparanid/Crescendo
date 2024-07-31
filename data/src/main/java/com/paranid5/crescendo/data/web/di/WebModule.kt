package com.paranid5.crescendo.data.web.di

import com.paranid5.crescendo.data.web.OpenBrowserUseCaseImpl
import com.paranid5.crescendo.domain.web.OpenBrowserUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val webModule = module {
    singleOf(::OpenBrowserUseCaseImpl) bind OpenBrowserUseCase::class
}