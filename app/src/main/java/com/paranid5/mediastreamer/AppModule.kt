package com.paranid5.mediastreamer

import android.content.Context
import android.os.Build
import com.paranid5.mediastreamer.domain.ktor_client.KtorClient
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.domain.stream_service.StreamServiceAccessor
import com.paranid5.mediastreamer.domain.video_cash_service.VideoCashServiceAccessor
import com.paranid5.mediastreamer.presentation.appbar.stream_button.StreamButtonUIHandler
import com.paranid5.mediastreamer.presentation.main_activity.MainActivityViewModel
import com.paranid5.mediastreamer.presentation.search_stream.SearchStreamPresenter
import com.paranid5.mediastreamer.presentation.search_stream.SearchStreamUIHandler
import com.paranid5.mediastreamer.presentation.search_stream.SearchStreamViewModel
import com.paranid5.mediastreamer.presentation.streaming.StreamingPresenter
import com.paranid5.mediastreamer.presentation.streaming.StreamingUIHandler
import com.paranid5.mediastreamer.presentation.streaming.StreamingViewModel
import com.paranid5.mediastreamer.presentation.ui.GlideUtils
import com.paranid5.mediastreamer.presentation.ui.permissions.ExternalStorageDescriptionProvider
import com.paranid5.mediastreamer.presentation.ui.permissions.PostNotificationDescriptionProvider
import com.paranid5.mediastreamer.presentation.ui.permissions.externalStoragePermissionQueue
import com.paranid5.mediastreamer.presentation.ui.permissions.postNotificationsQueue
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val STREAM_WITH_NO_NAME = "stream_no_name"
const val UNKNOWN_STREAMER = "unknown_streamer"
const val EXTERNAL_STORAGE_PERMISSION_QUEUE = "external_storage_permission_queue"
const val POST_NOTIFICATIONS_PERMISSION_QUEUE = "post_notifications_permission_queue"
const val IS_PLAYING_STATE = "is_playing_state"

private val resourcesModule = module {
    factory(named(STREAM_WITH_NO_NAME)) {
        androidContext().resources.getString(R.string.stream_no_name)
    }

    factory(named(UNKNOWN_STREAMER)) {
        androidContext().resources.getString(R.string.unknown_streamer)
    }
}

private val serviceAccessors = module {
    singleOf(::StreamServiceAccessor)
    singleOf(::VideoCashServiceAccessor)
}

private val permissionDescriptionProviders = module {
    single {
        ExternalStorageDescriptionProvider(
            description = androidContext().resources
                .getString(R.string.external_storage_description)
        )
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) single {
        PostNotificationDescriptionProvider(
            description = androidContext().resources.getString(R.string.notifications_description)
        )
    }
}

private val permissionQueues = module {
    single(named(EXTERNAL_STORAGE_PERMISSION_QUEUE)) { externalStoragePermissionQueue }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        single(named(POST_NOTIFICATIONS_PERMISSION_QUEUE)) { postNotificationsQueue }
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

private val uiModule = module {
    includes(searchStreamModule, streamingModule)
    singleOf(::StreamButtonUIHandler)
}

val appModule = module {
    includes(globalsModule, uiModule)
    viewModelOf(::MainActivityViewModel)
}