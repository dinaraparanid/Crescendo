package com.paranid5.crescendo.data.datastore.di

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val dataStoreModule = module {
    singleOf(::DataStoreProvider)
}