package com.paranid5.crescendo.data.cover.di

import com.paranid5.crescendo.data.cover.CoverRetrieverImpl
import com.paranid5.crescendo.domain.cover.CoverRetriever
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coverRetrieverModule = module {
    singleOf(::CoverRetrieverImpl) bind CoverRetriever::class
}
