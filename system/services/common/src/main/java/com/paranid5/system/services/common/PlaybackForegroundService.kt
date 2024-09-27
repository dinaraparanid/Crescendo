package com.paranid5.system.services.common

import android.app.Notification
import android.app.Service
import android.content.pm.ServiceInfo
import android.os.Build

interface PlaybackForegroundService

fun <S> S.startMediaForeground(id: Int, notification: Notification)
        where S : Service,
              S : PlaybackForegroundService = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
        startForeground(id, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)

    else -> startForeground(id, notification)
}
