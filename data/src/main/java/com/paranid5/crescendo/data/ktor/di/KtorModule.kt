package com.paranid5.crescendo.data.ktor.di

import com.paranid5.crescendo.data.ktor.KtorClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val ktorModule = module {
    singleOf(::KtorClient)
}