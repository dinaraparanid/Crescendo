package com.paranid5.crescendo.data.di

import com.paranid5.crescendo.data.audio_effects.di.audioEffectsModule
import com.paranid5.crescendo.data.current_playlist.di.currentPlaylistModule
import com.paranid5.crescendo.data.datastore.di.dataStoreModule
import com.paranid5.crescendo.data.genius.di.geniusModule
import com.paranid5.crescendo.data.github.di.gitHubModule
import com.paranid5.crescendo.data.image.di.imageRetrieverModule
import com.paranid5.crescendo.data.ktor.di.ktorModule
import com.paranid5.crescendo.data.metadata.di.metadataModule
import com.paranid5.crescendo.data.playback.di.playbackModule
import com.paranid5.crescendo.data.stream.di.streamModule
import com.paranid5.crescendo.data.tags.di.tagsModule
import com.paranid5.crescendo.data.tracks.di.tracksModule
import com.paranid5.crescendo.data.waveform.di.waveformModule
import com.paranid5.crescendo.data.web.di.webModule
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val dataModule = module {
    includes(
        audioEffectsModule,
        currentPlaylistModule,
        dataStoreModule,
        geniusModule,
        gitHubModule,
        imageRetrieverModule,
        ktorModule,
        metadataModule,
        playbackModule,
        streamModule,
        tagsModule,
        tracksModule,
        waveformModule,
        webModule,
    )

    single { Json { ignoreUnknownKeys = true } }
}
