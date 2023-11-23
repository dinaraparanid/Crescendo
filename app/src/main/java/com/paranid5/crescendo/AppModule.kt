@file:Suppress("LongLine")

package com.paranid5.crescendo

import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import com.paranid5.crescendo.data.eq.EqualizerData
import com.paranid5.crescendo.domain.StorageHandler
import com.paranid5.crescendo.domain.ktor_client.KtorClient
import com.paranid5.crescendo.domain.services.stream_service.StreamServiceAccessor
import com.paranid5.crescendo.domain.services.track_service.TrackServiceAccessor
import com.paranid5.crescendo.domain.services.video_cache_service.VideoCacheServiceAccessor
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsUIHandler
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsViewModel
import com.paranid5.crescendo.presentation.main.fetch_stream.FetchStreamUIHandler
import com.paranid5.crescendo.presentation.main.fetch_stream.FetchStreamViewModel
import com.paranid5.crescendo.presentation.main.MainActivityViewModel
import com.paranid5.crescendo.presentation.main.playing.PlayingUIHandler
import com.paranid5.crescendo.presentation.main.tracks.TracksUIHandler
import com.paranid5.crescendo.presentation.main.tracks.TracksViewModel
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.ui.permissions.audioRecordingPermissionQueue
import com.paranid5.crescendo.presentation.ui.permissions.description_providers.AudioRecordingDescriptionProvider
import com.paranid5.crescendo.presentation.ui.permissions.description_providers.ExternalStorageDescriptionProvider
import com.paranid5.crescendo.presentation.ui.permissions.description_providers.ForegroundServiceDescriptionProvider
import com.paranid5.crescendo.presentation.ui.permissions.externalStoragePermissionQueue
import com.paranid5.crescendo.presentation.ui.permissions.foregroundServicePermissionQueue
import com.paranid5.crescendo.presentation.ui.utils.CoilUtils
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

const val STREAM_WITH_NO_NAME = "stream_no_name"
const val UNKNOWN_STREAMER = "unknown_streamer"
const val IS_PLAYING = "is_playing_state"
const val AUDIO_SESSION_ID = "audio_session_id"
const val EQUALIZER_DATA = "equalizer_data"

const val STREAM_SERVICE_CONNECTION = "stream_service_connection"
const val TRACK_SERVICE_CONNECTION = "track_service_connection"
const val VIDEO_CASH_SERVICE_CONNECTION = "video_cash_service_connection"

const val EXTERNAL_STORAGE_PERMISSION_QUEUE = "external_storage_permission_queue"
const val FOREGROUND_SERVICE_PERMISSION_QUEUE = "foreground_service_permission_queue"
const val AUDIO_RECORDING_PERMISSION_QUEUE = "audio_recording_permission_queue"

private fun Scope.getString(@StringRes strRes: Int) =
    androidContext().resources.getString(strRes)

private val resourcesModule = module {
    factory(named(STREAM_WITH_NO_NAME)) { getString(R.string.stream_no_name) }
    factory(named(UNKNOWN_STREAMER)) { getString(R.string.unknown_streamer) }
}

private val serviceAccessorsModule = module {
    singleOf(::StreamServiceAccessor)
    singleOf(::TrackServiceAccessor)
    singleOf(::VideoCacheServiceAccessor)
}

private val serviceConnectionsModule = module {
    single(named(STREAM_SERVICE_CONNECTION)) { MutableStateFlow(false) }
    single(named(TRACK_SERVICE_CONNECTION)) { MutableStateFlow(false) }
    single(named(VIDEO_CASH_SERVICE_CONNECTION)) { MutableStateFlow(false) }
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

private val permissionQueues = module {
    single(named(EXTERNAL_STORAGE_PERMISSION_QUEUE)) { externalStoragePermissionQueue }
    single(named(AUDIO_RECORDING_PERMISSION_QUEUE)) { audioRecordingPermissionQueue }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        single(named(FOREGROUND_SERVICE_PERMISSION_QUEUE)) { foregroundServicePermissionQueue }
}

private val permissionsModule = module {
    includes(permissionDescriptionProviders, permissionQueues)
}

private val globalsModule = module {
    includes(resourcesModule, servicesModule, permissionsModule)

    singleOf(::StorageHandler)
    single { androidApplication() as MainApplication }
    factory { (context: Context, _: Boolean?) -> CoilUtils(context) }
    singleOf(::KtorClient)

    single(named(IS_PLAYING)) { MutableStateFlow(false) }
    single(named(AUDIO_SESSION_ID)) { MutableStateFlow(0) }
    single(named(EQUALIZER_DATA)) { MutableStateFlow<EqualizerData?>(null) }
}

private val searchStreamModule = module {
    singleOf(::FetchStreamUIHandler)
    viewModelOf(::FetchStreamViewModel)
}

private val playingModule = module {
    singleOf(::PlayingUIHandler)
}

private val audioEffectsModule = module {
    singleOf(::AudioEffectsUIHandler)
    viewModelOf(::AudioEffectsViewModel)
}

private val trimmerModule = module {
    viewModelOf(::TrimmerViewModel)
}

private val tracksModule = module {
    singleOf(::TracksUIHandler)
    viewModelOf(::TracksViewModel)
}

private val uiMainModule = module {
    includes(
        searchStreamModule, playingModule,
        audioEffectsModule, trimmerModule,
        tracksModule
    )
}

private val uiModule = module {
    includes(uiMainModule)
}

val appModule = module {
    includes(globalsModule, uiModule)
    viewModelOf(::MainActivityViewModel)
}