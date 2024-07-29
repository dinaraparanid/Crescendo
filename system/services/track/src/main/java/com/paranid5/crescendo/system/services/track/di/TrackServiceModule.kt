package com.paranid5.crescendo.system.services.track.di

import com.paranid5.crescendo.core.impl.di.TRACK_SERVICE_CONNECTION
import com.paranid5.crescendo.system.services.track.TrackServiceAccessor
import com.paranid5.crescendo.system.services.track.TrackServiceInteractor
import com.paranid5.crescendo.system.services.track.notification.NotificationManager
import com.paranid5.crescendo.system.services.track.playback.PlayerProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val trackServiceModule = module {
    single {
        TrackServiceAccessor(
            context = androidContext(),
            isTrackServiceConnectedState = get(named(TRACK_SERVICE_CONNECTION)),
        )
    } bind TrackServiceInteractor::class

    factory { params -> PlayerProvider(params.get(), get(), get(), get(), get()) }
    factory { params -> NotificationManager(params.get(), get()) }
}