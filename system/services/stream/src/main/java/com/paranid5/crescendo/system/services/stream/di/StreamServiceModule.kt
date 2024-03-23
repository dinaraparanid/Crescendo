package com.paranid5.crescendo.system.services.stream.di

import com.paranid5.crescendo.system.services.stream.StreamServiceAccessor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val streamServiceModule = module {
    singleOf(::StreamServiceAccessor)
}