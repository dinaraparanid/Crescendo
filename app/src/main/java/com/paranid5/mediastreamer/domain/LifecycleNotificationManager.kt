package com.paranid5.mediastreamer.domain

import android.os.Build
import androidx.annotation.RequiresApi

interface LifecycleNotificationManager : NotificationManager {
    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel()

    /**
     * Runs loop that observers all notification related states
     * and updates notification when something has changed
     */

    suspend fun startNotificationObserving()
}