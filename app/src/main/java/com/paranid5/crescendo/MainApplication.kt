package com.paranid5.crescendo

import android.app.Application
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application(), CoroutineScope by CoroutineScope(Dispatchers.IO) {
    override fun onCreate() {
        super.onCreate()
        initKoin()
        launch(Dispatchers.IO) { initYtDl() }
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }

    private fun initYtDl() = runCatching {
        YoutubeDL.getInstance().apply {
            init(this@MainApplication)
            updateYoutubeDL(this@MainApplication)
        }
    }
}