package com.paranid5.crescendo.services.track_service.notification

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.paranid5.crescendo.services.track_service.TrackService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

suspend inline fun TrackService.startNotificationMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        notificationManager
            .currentTrackFlow
            .distinctUntilChanged()
            .collectLatest {
                serviceScope.launch { notificationManager.updateNotification() }
            }
    }