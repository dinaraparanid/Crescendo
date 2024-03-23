package com.paranid5.crescendo.system.services.video_cache.di

import com.paranid5.crescendo.system.services.video_cache.VideoCacheServiceAccessor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val videoCacheServiceModule = module {
    singleOf(::VideoCacheServiceAccessor)
}