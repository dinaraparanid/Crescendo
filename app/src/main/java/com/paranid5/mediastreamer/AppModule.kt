package com.paranid5.mediastreamer

import android.content.Context
import com.paranid5.mediastreamer.presentation.appbar.stream_button.StreamButtonUIHandler
import com.paranid5.mediastreamer.presentation.search_stream.SearchStreamPresenter
import com.paranid5.mediastreamer.presentation.search_stream.SearchStreamUIHandler
import com.paranid5.mediastreamer.presentation.search_stream.SearchStreamViewModel
import com.paranid5.mediastreamer.presentation.streaming.StreamingPresenter
import com.paranid5.mediastreamer.presentation.streaming.StreamingUIHandler
import com.paranid5.mediastreamer.presentation.streaming.StreamingViewModel
import com.paranid5.mediastreamer.stream_service.StreamServiceAccessor
import com.paranid5.mediastreamer.utils.GlideUtils
import com.paranid5.mediastreamer.video_cash_service.VideoCashServiceAccessor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val STREAM_WITH_NO_NAME = "stream_no_name"
const val UNKNOWN_STREAMER = "unknown_streamer"

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

private val globalsModule = module {
    includes(resourcesModule, serviceAccessors)

    singleOf(::StorageHandler)
    single { androidApplication() as MainApplication }
    factory { (context: Context) -> GlideUtils(context) }
    singleOf(::KtorClient)
}

private val searchStreamModule = module {
    singleOf(::SearchStreamUIHandler)
    factory { (currentText: String?) -> SearchStreamPresenter(currentText) }
    viewModelOf(::SearchStreamViewModel)
}

private val streamingModule = module {
    singleOf(::StreamingUIHandler)
    factoryOf(::StreamingPresenter)
    viewModelOf(::StreamingViewModel)
}

private val uiModule = module {
    includes(searchStreamModule, streamingModule)
    singleOf(::StreamButtonUIHandler)
}

val appModule = module {
    includes(globalsModule, uiModule)
}