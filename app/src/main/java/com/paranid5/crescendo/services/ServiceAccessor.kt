package com.paranid5.crescendo.services

import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.domain.utils.extensions.sendBroadcastCompat

interface ServiceAccessor {
    val appContext: Context
    fun sendBroadcast(intent: Intent)
    fun sendBroadcast(action: String)
}

class ServiceAccessorImpl(override val appContext: Context) : ServiceAccessor {
    override fun sendBroadcast(intent: Intent) {
        appContext.sendBroadcastCompat(intent)
    }

    override fun sendBroadcast(action: String) =
        sendBroadcast(Intent(action))
}