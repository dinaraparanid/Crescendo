package com.paranid5.crescendo

import android.os.Build
import androidx.annotation.StringRes
import com.paranid5.crescendo.audio_effects.di.audioEffectsModule
import com.paranid5.crescendo.cache.di.cacheModule
import com.paranid5.crescendo.core.impl.di.AUDIO_SESSION_ID
import com.paranid5.crescendo.core.impl.di.IS_PLAYING
import com.paranid5.crescendo.core.impl.di.STREAM_SERVICE_CONNECTION
import com.paranid5.crescendo.core.impl.di.STREAM_WITH_NO_NAME
import com.paranid5.crescendo.core.impl.di.TRACK_SERVICE_CONNECTION
import com.paranid5.crescendo.core.impl.di.UNKNOWN_STREAMER
import com.paranid5.crescendo.core.impl.di.VIDEO_CACHE_SERVICE_CONNECTION
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.current_playlist.di.currentPlaylistModule
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.dataModule
import com.paranid5.crescendo.data.ktor_client.KtorClient
import com.paranid5.crescendo.fetch_stream.di.fetchStreamModule
import com.paranid5.crescendo.playing.di.playingModule
import com.paranid5.crescendo.system.services.stream.di.streamServiceModule
import com.paranid5.crescendo.system.services.track.di.trackServiceModule
import com.paranid5.crescendo.system.services.video_cache.di.videoCacheServiceModule
import com.paranid5.crescendo.tracks.di.tracksModule
import com.paranid5.crescendo.trimmer.di.trimmerModule
import com.paranid5.crescendo.ui.permissions.description_providers.AudioRecordingDescriptionProvider
import com.paranid5.crescendo.ui.permissions.description_providers.ExternalStorageDescriptionProvider
import com.paranid5.crescendo.ui.permissions.description_providers.ForegroundServiceDescriptionProvider
import com.paranid5.crescendo.ui.permissions.di.permissionQueuesModule
import com.paranid5.system.services.common.di.commonServiceModule
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

private fun Scope.getString(@StringRes strRes: Int) =
    androidContext().resources.getString(strRes)

private val resourcesModule = module {
    factory(named(STREAM_WITH_NO_NAME)) { getString(R.string.stream_no_name) }
    factory(named(UNKNOWN_STREAMER)) { getString(R.string.unknown_streamer) }
}

private val serviceAccessorsModule = module {
    includes(
        commonServiceModule,
        streamServiceModule,
        trackServiceModule,
        videoCacheServiceModule
    )
}

private val serviceConnectionsModule = module {
    single(named(STREAM_SERVICE_CONNECTION)) { MutableStateFlow(false) }
    single(named(TRACK_SERVICE_CONNECTION)) { MutableStateFlow(false) }
    single(named(VIDEO_CACHE_SERVICE_CONNECTION)) { MutableStateFlow(false) }
}

private val servicesModule = module {
    includes(serviceAccessorsModule, serviceConnectionsModule)
}

private val permissionDescriptionProviders = module {
    single {
        ExternalStorageDescriptionProvider(
            description = getString(R.string.external_storage_description)
        )
    }

    single {
        AudioRecordingDescriptionProvider(
            description = getString(R.string.audio_recording_description)
        )
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) single {
        ForegroundServiceDescriptionProvider(
            description = getString(R.string.notifications_description)
        )
    }
}

private val permissionsModule = module {
    includes(permissionDescriptionProviders, permissionQueuesModule)
}

private val globalsModule = module {
    includes(resourcesModule, servicesModule, permissionsModule)

    singleOf(::StorageRepository)
    single { androidApplication() as MainApplication }
    singleOf(::KtorClient)

    single(named(IS_PLAYING)) { MutableStateFlow(false) }
    single(named(AUDIO_SESSION_ID)) { MutableStateFlow(0) }
}

private val uiModule = module {
    includes(
        fetchStreamModule, playingModule,
        audioEffectsModule, trimmerModule,
        tracksModule, currentPlaylistModule,
        cacheModule
    )
}

val appModule = module {
    includes(globalsModule, uiModule, dataModule)
}