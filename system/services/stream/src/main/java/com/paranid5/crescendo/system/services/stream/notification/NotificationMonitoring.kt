package com.paranid5.crescendo.system.services.stream.notification

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.paranid5.crescendo.system.services.stream.StreamService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

internal suspend inline fun StreamService.startNotificationMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        notificationManager
            .currentMetadataFlow
            .distinctUntilChanged()
            .collectLatest {
                serviceScope.launch { notificationManager.updateNotification() }
            }
    }