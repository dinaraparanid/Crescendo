package com.paranid5.crescendo.data.datastore.di

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.playback.di.playbackModule
import com.paranid5.crescendo.data.datastore.AudioEffectsDataStore
import com.paranid5.crescendo.data.datastore.PlaybackDataStore
import com.paranid5.crescendo.data.datastore.StreamDataStore
import com.paranid5.crescendo.data.datastore.TracksDataStore
import com.paranid5.crescendo.data.datastore.WaveformDataStore
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val dataStoreModule = module {
    includes(playbackModule)

    singleOf(::DataStoreProvider)
    singleOf(::AudioEffectsDataStore)
    singleOf(::PlaybackDataStore)
    singleOf(::StreamDataStore)
    singleOf(::TracksDataStore)
    singleOf(::WaveformDataStore)
}
