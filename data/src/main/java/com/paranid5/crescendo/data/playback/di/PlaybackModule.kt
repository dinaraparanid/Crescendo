package com.paranid5.crescendo.data.playback.di

import com.paranid5.crescendo.data.playback.PlaybackRepositoryImpl
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val playbackModule = module {
    singleOf(::PlaybackRepositoryImpl) bind PlaybackRepository::class
}
