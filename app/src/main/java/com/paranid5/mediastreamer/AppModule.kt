package com.paranid5.mediastreamer

import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.domain.ktor_client.KtorClient
import com.paranid5.mediastreamer.data.eq.EqualizerData
import com.paranid5.mediastreamer.domain.stream_service.StreamServiceAccessor
import com.paranid5.mediastreamer.domain.video_cash_service.VideoCashServiceAccessor
import com.paranid5.mediastreamer.presentation.appbar.stream_button.StreamButtonUIHandler
import com.paranid5.mediastreamer.presentation.audio_effects.AudioEffectsPresenter
import com.paranid5.mediastreamer.presentation.audio_effects.AudioEffectsUIHandler
import com.paranid5.mediastreamer.presentation.audio_effects.AudioEffectsViewModel
import com.paranid5.mediastreamer.presentation.main_activity.MainActivityViewModel
import com.paranid5.mediastreamer.presentation.search_stream.SearchStreamPresenter
import com.paranid5.mediastreamer.presentation.search_stream.SearchStreamUIHandler
import com.paranid5.mediastreamer.presentation.search_stream.SearchStreamViewModel
import com.paranid5.mediastreamer.presentation.streaming.StreamingPresenter
import com.paranid5.mediastreamer.presentation.streaming.StreamingUIHandler
import com.paranid5.mediastreamer.presentation.streaming.StreamingViewModel
import com.paranid5.mediastreamer.presentation.ui.GlideUtils
import com.paranid5.mediastreamer.presentation.ui.permissions.audioRecordingPermissionQueue
import com.paranid5.mediastreamer.presentation.ui.permissions.description_providers.AudioRecordingDescriptionProvider
import com.paranid5.mediastreamer.presentation.ui.permissions.description_providers.ExternalStorageDescriptionProvider
import com.paranid5.mediastreamer.presentation.ui.permissions.description_providers.PostNotificationDescriptionProvider
import com.paranid5.mediastreamer.presentation.ui.permissions.externalStoragePermissionQueue
import com.paranid5.mediastreamer.presentation.ui.permissions.postNotificationsPermissionQueue
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
const val IS_PLAYING_STATE = "is_playing_state"
const val AUDIO_SESSION_ID = "audio_session_id"
const val EQUALIZER_DATA = "equalizer_data"

const val EXTERNAL_STORAGE_PERMISSION_QUEUE = "external_storage_permission_queue"
const val POST_NOTIFICATIONS_PERMISSION_QUEUE = "post_notifications_permission_queue"
const val AUDIO_RECORDING_PERMISSION_QUEUE = "audio_recording_permission_queue"

private fun Scope.getString(@StringRes strRes: Int) =
    androidContext().resources.getString(strRes)

private val resourcesModule = module {
    factory(named(STREAM_WITH_NO_NAME)) { getString(R.string.stream_no_name) }
    factory(named(UNKNOWN_STREAMER)) { getString(R.string.unknown_streamer) }
}

private val serviceAccessors = module {
    singleOf(::StreamServiceAccessor)
    singleOf(::VideoCashServiceAccessor)
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
        PostNotificationDescriptionProvider(
            description = getString(R.string.notifications_description)
        )
    }
}

private val permissionQueues = module {
    single(named(EXTERNAL_STORAGE_PERMISSION_QUEUE)) { externalStoragePermissionQueue }
    single(named(AUDIO_RECORDING_PERMISSION_QUEUE)) { audioRecordingPermissionQueue }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        single(named(POST_NOTIFICATIONS_PERMISSION_QUEUE)) { postNotificationsPermissionQueue }
}

private val permissions = module {
    includes(permissionDescriptionProviders, permissionQueues)
}

private val globalsModule = module {
    includes(resourcesModule, serviceAccessors, permissions)

    singleOf(::StorageHandler)
    single { androidApplication() as MainApplication }
    factory { (context: Context) -> GlideUtils(context) }
    singleOf(::KtorClient)
    single(named(IS_PLAYING_STATE)) { MutableStateFlow(false) }
    single(named(AUDIO_SESSION_ID)) { MutableStateFlow(0) }
    single(named(EQUALIZER_DATA)) { MutableStateFlow<EqualizerData?>(null) }
}

private val searchStreamModule = module {
    singleOf(::SearchStreamUIHandler)
    factory { (currentText: String?) -> SearchStreamPresenter(currentText) }
    viewModelOf(::SearchStreamViewModel)
}

private val streamingModule = module {
    singleOf(::StreamingUIHandler)
    factory { StreamingPresenter(get(named(IS_PLAYING_STATE))) }
    viewModelOf(::StreamingViewModel)
}

private val audioEffectsModule = module {
    singleOf(::AudioEffectsUIHandler)

    factory { (pitchText: String?, speedText: String?) ->
        AudioEffectsPresenter(pitchText, speedText)
    }

    viewModelOf(::AudioEffectsViewModel)
}

private val uiMainModule = module {
    includes(searchStreamModule, streamingModule, audioEffectsModule)
    singleOf(::StreamButtonUIHandler)
}

private val uiModule = module {
    includes(uiMainModule)
}

val appModule = module {
    includes(globalsModule, uiModule)
    viewModelOf(::MainActivityViewModel)
}