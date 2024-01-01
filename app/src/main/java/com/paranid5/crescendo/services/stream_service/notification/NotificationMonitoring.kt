package com.paranid5.crescendo.services.stream_service.notification

import android.app.Service
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.paranid5.crescendo.services.stream_service.StreamService2
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

suspend inline fun StreamService2.startNotificationMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        notificationManager
            .currentMetadataFlow
            .distinctUntilChanged()
            .collectLatest {
                serviceScope.launch { notificationManager.updateNotification() }
            }
    }

@Suppress("DEPRECATION")
fun StreamService2.detachNotification() = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->
        stopForeground(Service.STOP_FOREGROUND_REMOVE)

    else -> stopForeground(true)
}