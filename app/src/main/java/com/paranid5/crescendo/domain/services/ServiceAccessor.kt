package com.paranid5.crescendo.domain.services

import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.MainApplication
import org.koin.core.component.KoinComponent

abstract class ServiceAccessor(protected val application: MainApplication) : KoinComponent {
    protected inline val appContext: Context
        get() = application.applicationContext

    protected fun sendBroadcast(intent: Intent) = application.sendBroadcast(intent)
    protected fun sendBroadcast(action: String) = sendBroadcast(Intent(action))
}