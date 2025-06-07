package com.paranid5.crescendo

import android.app.Application
import com.paranid5.crescendo.di.appModule
import com.paranid5.crescendo.utils.extensions.launchInScope
import com.paranid5.crescendo.utils.extensions.runCatchingNonCancellation
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        initKoin()
        applicationScope.launchInScope(Dispatchers.IO) { initYtDl() }
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }

    private suspend fun initYtDl() = runCatchingNonCancellation {
        withContext(Dispatchers.IO) {
            YoutubeDL.getInstance().let { ytdl ->
                ytdl.init(this@MainApplication)
                ytdl.updateYoutubeDL(this@MainApplication)
            }
        }
    }
}
