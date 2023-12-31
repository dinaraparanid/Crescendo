package com.paranid5.crescendo.services

import android.content.Context
import android.content.Intent

interface ServiceAccessor {
    val appContext: Context
    fun sendBroadcast(intent: Intent)
    fun sendBroadcast(action: String)
}

class ServiceAccessorImpl(override val appContext: Context) : ServiceAccessor {
    override fun sendBroadcast(intent: Intent) =
        appContext.sendBroadcast(intent)

    override fun sendBroadcast(action: String) =
        sendBroadcast(Intent(action))
}