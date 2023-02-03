package com.paranid5.mediastreamer

import android.content.Context
import com.paranid5.mediastreamer.presentation.search_stream.SearchStreamPresenter
import com.paranid5.mediastreamer.presentation.streaming.StreamingPresenter
import com.paranid5.mediastreamer.presentation.search_stream.SearchStreamUIHandler
import com.paranid5.mediastreamer.presentation.appbar.stream_button.StreamButtonUIHandler
import com.paranid5.mediastreamer.presentation.streaming.StreamingUIHandler
import com.paranid5.mediastreamer.presentation.search_stream.SearchStreamViewModel
import com.paranid5.mediastreamer.presentation.streaming.StreamingViewModel
import com.paranid5.mediastreamer.stream_service.StreamServiceAccessor
import com.paranid5.mediastreamer.utils.GlideUtils
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val STREAM_WITH_NO_NAME = "stream_no_name"
const val UNKNOWN_STREAMER = "unknown_streamer"

val resourcesModule = module {
    factory(named(STREAM_WITH_NO_NAME)) {
        androidContext().resources.getString(R.string.stream_no_name)
    }

    factory(named(UNKNOWN_STREAMER)) {
        androidContext().resources.getString(R.string.unknown_streamer)
    }
}

val globalsModule = module {
    includes(resourcesModule)

    singleOf(::StorageHandler)
    single { androidApplication() as MainApplication }
    singleOf(::StreamServiceAccessor)
    factory { (context: Context) -> GlideUtils(context) }
}

val searchStreamModule = module {
    singleOf(::SearchStreamUIHandler)
    factory { (currentText: String?) -> SearchStreamPresenter(currentText) }
    viewModelOf(::SearchStreamViewModel)
}

val streamingModule = module {
    singleOf(::StreamingUIHandler)
    factoryOf(::StreamingPresenter)
    viewModelOf(::StreamingViewModel)
}

val uiModule = module {
    includes(searchStreamModule, streamingModule)
    singleOf(::StreamButtonUIHandler)
}

val appModule = module {
    includes(globalsModule, uiModule)
}