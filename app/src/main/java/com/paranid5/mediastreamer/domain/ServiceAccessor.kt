package com.paranid5.mediastreamer.domain

import android.content.Context
import android.content.Intent
import com.paranid5.mediastreamer.MainApplication

abstract class ServiceAccessor(protected val application: MainApplication) {
    protected inline val appContext: Context
        get() = application.applicationContext

    protected fun sendBroadcast(intent: Intent) = application.sendBroadcast(intent)
    protected fun sendBroadcast(action: String) = sendBroadcast(Intent(action))
}