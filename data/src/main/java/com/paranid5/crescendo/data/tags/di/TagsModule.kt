package com.paranid5.crescendo.data.tags.di

import com.paranid5.crescendo.data.tags.TagsRepositoryImpl
import com.paranid5.crescendo.domain.tags.TagsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val tagsModule = module {
    singleOf(::TagsRepositoryImpl) bind TagsRepository::class
}
