package com.paranid5.crescendo.services.stream_service.notification

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.paranid5.crescendo.services.stream_service.StreamService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

suspend inline fun StreamService.startNotificationMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        notificationManager
            .currentMetadataFlow
            .distinctUntilChanged()
            .collectLatest {
                serviceScope.launch { notificationManager.updateNotification() }
            }
    }