package com.paranid5.crescendo.system.services.stream.di

import com.paranid5.crescendo.system.services.stream.StreamServiceAccessor
import com.paranid5.crescendo.system.services.stream.extractor.UrlExtractor
import com.paranid5.crescendo.system.services.stream.notification.NotificationManager
import com.paranid5.crescendo.system.services.stream.playback.PlayerProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val streamServiceModule = module {
    singleOf(::StreamServiceAccessor)
    single { params -> PlayerProvider(params.get(), get()) }
    singleOf(::UrlExtractor)
    single { params -> NotificationManager(params.get(), get()) }
}