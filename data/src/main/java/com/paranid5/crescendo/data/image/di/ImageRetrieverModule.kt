package com.paranid5.crescendo.data.image.di

import com.paranid5.crescendo.data.image.ImageRetrieverImpl
import com.paranid5.crescendo.domain.image.ImageRetriever
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val imageRetrieverModule = module {
    singleOf(::ImageRetrieverImpl) bind ImageRetriever::class
}
