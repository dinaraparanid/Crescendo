package com.paranid5.crescendo.di

import android.os.Build
import androidx.annotation.StringRes
import com.paranid5.crescendo.audio_effects.di.audioEffectsModule
import com.paranid5.crescendo.cache.di.cacheModule
import com.paranid5.crescendo.core.impl.di.STREAM_SERVICE_CONNECTION
import com.paranid5.crescendo.core.impl.di.TRACK_SERVICE_CONNECTION
import com.paranid5.crescendo.core.impl.di.VIDEO_CACHE_SERVICE_CONNECTION
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.data.di.dataModule
import com.paranid5.crescendo.feature.current_playlist.di.currentPlaylistModule
import com.paranid5.crescendo.feature.play.main.di.playModule
import com.paranid5.crescendo.feature.playing.di.playingModule
import com.paranid5.crescendo.feature.stream.di.streamModule
import com.paranid5.crescendo.system.services.stream.di.streamServiceModule
import com.paranid5.crescendo.system.services.track.di.trackServiceModule
import com.paranid5.crescendo.system.services.video_cache.di.videoCacheServiceModule
import com.paranid5.crescendo.tracks.di.tracksModule
import com.paranid5.crescendo.trimmer.di.trimmerModule
import com.paranid5.crescendo.ui.permissions.description_providers.AudioRecordingDescriptionProvider
import com.paranid5.crescendo.ui.permissions.description_providers.ExternalStorageDescriptionProvider
import com.paranid5.crescendo.ui.permissions.description_providers.ForegroundServiceDescriptionProvider
import com.paranid5.crescendo.ui.permissions.di.permissionQueuesModule
import com.paranid5.crescendo.view_model.MainViewModelImpl
import com.paranid5.system.services.common.di.commonServiceModule
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

private fun Scope.getString(@StringRes strRes: Int) =
    androidContext().getString(strRes)

private val serviceAccessorsModule = module {
    includes(
        commonServiceModule,
        streamServiceModule,
        trackServiceModule,
        videoCacheServiceModule,
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

private val systemModule = module {
    includes(servicesModule, permissionsModule)
}

private val featureModule = module {
    includes(
        streamModule, playingModule,
        audioEffectsModule, trimmerModule,
        tracksModule, currentPlaylistModule,
        cacheModule, playModule,
    )
}

internal val appModule = module {
    includes(systemModule, featureModule, dataModule)
    viewModelOf(::MainViewModelImpl)
}
