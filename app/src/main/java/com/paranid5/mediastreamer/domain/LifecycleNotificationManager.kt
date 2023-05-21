package com.paranid5.mediastreamer.domain

interface LifecycleNotificationManager : NotificationManager {

    /**
     * Runs loop that observers all notification related states
     * and updates notification when something has changed
     */

    suspend fun startNotificationObserving()
}