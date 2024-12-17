package com.paranid5.crescendo.data.files.di

import com.paranid5.crescendo.data.files.MediaFilesRepositoryImpl
import com.paranid5.crescendo.domain.files.MediaFilesRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val mediaFilesModule = module {
    singleOf(::MediaFilesRepositoryImpl) bind MediaFilesRepository::class
}
