package com.paranid5.crescendo.system.services.video_cache.di

import com.paranid5.crescendo.system.services.video_cache.VideoCacheServiceAccessor
import com.paranid5.crescendo.system.services.video_cache.cache.CacheManager
import com.paranid5.crescendo.system.services.video_cache.extractor.UrlExtractor
import com.paranid5.crescendo.system.services.video_cache.files.InitMediaFileUseCase
import com.paranid5.crescendo.system.services.video_cache.files.MediaFileDownloader
import com.paranid5.crescendo.system.services.video_cache.files.VideoQueueManager
import com.paranid5.crescendo.system.services.video_cache.notification.NotificationManager
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val videoCacheServiceModule = module {
    singleOf(::VideoCacheServiceAccessor)
    singleOf(::InitMediaFileUseCase)
    factoryOf(::VideoQueueManager)
    factory { params -> NotificationManager(params.get()) }
    factoryOf(::CacheManager)
    factoryOf(::UrlExtractor)
    factoryOf(::MediaFileDownloader)
}
