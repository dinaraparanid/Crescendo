package com.paranid5.mediastreamer

import android.content.Context
import android.content.Intent

abstract class ServiceAccessor(protected val application: MainApplication) {
    protected inline val appContext: Context
        get() = application.applicationContext

    protected fun sendBroadcast(intent: Intent) = application.sendBroadcast(intent)
    protected fun sendBroadcast(action: String) = sendBroadcast(Intent(action))
}