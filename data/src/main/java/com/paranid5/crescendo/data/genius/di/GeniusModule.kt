package com.paranid5.crescendo.data.genius.di

import com.paranid5.crescendo.data.genius.GeniusApiImpl
import com.paranid5.crescendo.data.genius.GeniusApiUrlBuilder
import com.paranid5.crescendo.domain.genius.GeniusApi
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val geniusModule = module {
    singleOf(::GeniusApiImpl) bind GeniusApi::class
    singleOf(::GeniusApiUrlBuilder)
}
