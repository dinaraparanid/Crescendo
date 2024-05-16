package com.paranid5.crescendo.system.services.track.di

import com.paranid5.crescendo.system.services.track.TrackServiceAccessor
import com.paranid5.crescendo.system.services.track.notification.NotificationManager
import com.paranid5.crescendo.system.services.track.playback.PlayerProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val trackServiceModule = module {
    singleOf(::TrackServiceAccessor)
    single { params -> PlayerProvider(params.get(), get(), get()) }
    single { params -> NotificationManager(params.get(), get(), get()) }
}