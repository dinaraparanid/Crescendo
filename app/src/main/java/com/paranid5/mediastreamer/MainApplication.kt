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
        private const val STREAM_SERVICE_NAME = ".StreamService"
    }

    var isStreamServiceConnected = false
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

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}