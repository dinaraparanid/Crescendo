package com.paranid5.mediastreamer.presentation.ui.permissions

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Queue

inline val externalStoragePermissionQueue: Queue<String>
    get() = java.util.ArrayDeque(
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> mutableListOf(
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )

            else -> mutableListOf(Manifest.permission.READ_EXTERNAL_STORAGE).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    )

inline val postNotificationsQueue: Queue<String>
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    get() = java.util.ArrayDeque(mutableListOf(Manifest.permission.POST_NOTIFICATIONS))