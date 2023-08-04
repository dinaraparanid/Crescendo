package com.paranid5.crescendo.domain

interface LifecycleNotificationManager : NotificationManager {

    /**
     * Runs loop that observers all notification related states
     * and updates notification when something has changed
     */

    suspend fun startNotificationObserving()
}