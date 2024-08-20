package com.paranid5.crescendo.data.di

import com.paranid5.crescendo.data.audio_effects.di.audioEffectsModule
import com.paranid5.crescendo.data.cover.di.coverRetrieverModule
import com.paranid5.crescendo.data.current_playlist.di.currentPlaylistModule
import com.paranid5.crescendo.data.datastore.di.dataStoreModule
import com.paranid5.crescendo.data.github.di.gitHubModule
import com.paranid5.crescendo.data.ktor.di.ktorModule
import com.paranid5.crescendo.data.playback.di.playbackModule
import com.paranid5.crescendo.data.stream.di.streamModule
import com.paranid5.crescendo.data.tracks.di.tracksModule
import com.paranid5.crescendo.data.waveform.di.waveformModule
import com.paranid5.crescendo.data.web.di.webModule
import org.koin.dsl.module

val dataModule = module {
    includes(
        dataStoreModule,
        ktorModule,
        audioEffectsModule,
        currentPlaylistModule,
        playbackModule,
        streamModule,
        tracksModule,
        waveformModule,
        webModule,
        gitHubModule,
        coverRetrieverModule,
    )
}
