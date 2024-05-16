package com.paranid5.system.services.common.di

import com.paranid5.system.services.common.media_session.MediaSessionManager
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val commonServiceModule = module {
    factoryOf(::MediaSessionManager)
}