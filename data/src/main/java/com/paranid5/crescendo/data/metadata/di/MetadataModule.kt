package com.paranid5.crescendo.data.metadata.di

import com.paranid5.crescendo.data.metadata.MetadataExtractorImpl
import com.paranid5.crescendo.domain.metadata.MetadataExtractor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val metadataModule = module {
    singleOf(::MetadataExtractorImpl) bind MetadataExtractor::class
}
