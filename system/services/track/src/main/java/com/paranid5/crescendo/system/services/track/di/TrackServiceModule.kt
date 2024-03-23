package com.paranid5.crescendo.system.services.track.di

import com.paranid5.crescendo.system.services.track.TrackServiceAccessor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val trackServiceModule = module {
    singleOf(::TrackServiceAccessor)
}