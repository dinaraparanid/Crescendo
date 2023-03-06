package com.paranid5.mediastreamer.presentation.ui.permissions

import android.Manifest
import android.os.Build
import java.util.Queue

inline val externalStoragePermissionQueue: Queue<String>
    get() = java.util.ArrayDeque(
        mutableListOf(Manifest.permission.READ_EXTERNAL_STORAGE).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    )