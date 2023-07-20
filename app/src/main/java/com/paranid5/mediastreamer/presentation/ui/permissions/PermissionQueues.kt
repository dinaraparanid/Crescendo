package com.paranid5.mediastreamer.presentation.ui.permissions

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Queue

inline val externalStoragePermissionQueue: Queue<String>
    get() = java.util.ArrayDeque(
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES
            )

            else -> mutableListOf(Manifest.permission.READ_EXTERNAL_STORAGE).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    )

inline val foregroundServicePermissionQueue: Queue<String>
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    get() = java.util.ArrayDeque(
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> listOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK
            )

            else -> listOf(Manifest.permission.POST_NOTIFICATIONS)
        }
    )

inline val audioRecordingPermissionQueue: Queue<String>
    get() = java.util.ArrayDeque(
        listOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        )
    )