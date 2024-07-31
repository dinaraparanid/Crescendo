package com.paranid5.crescendo.data.ktor_client.di

import com.paranid5.crescendo.data.ktor_client.KtorClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val ktorModule = module {
    singleOf(::KtorClient)
}