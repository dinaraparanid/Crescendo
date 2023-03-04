package com.paranid5.mediastreamer

import android.app.Application
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    companion object {
        private const val STREAM_SERVICE_NAME = ".stream_service.StreamService"
        private const val VIDEO_CASH_SERVICE_NAME = ".video_cash_service.VideoCashService"
    }

    @Volatile
    var isStreamServiceConnected = false
        private set

    @Volatile
    var isVideoCashServiceConnected = false
        private set

    @JvmField
    val streamServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            if (name.shortClassName == STREAM_SERVICE_NAME)
                isStreamServiceConnected = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            if (name.shortClassName == STREAM_SERVICE_NAME)
                isStreamServiceConnected = false
        }
    }

    @JvmField
    val videoCashServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            if (name.shortClassName == VIDEO_CASH_SERVICE_NAME)
                isVideoCashServiceConnected = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            if (name.shortClassName == VIDEO_CASH_SERVICE_NAME)
                isVideoCashServiceConnected = false
        }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}