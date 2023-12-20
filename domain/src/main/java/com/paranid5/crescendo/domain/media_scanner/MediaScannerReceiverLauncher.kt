package com.paranid5.crescendo.domain.media_scanner

import android.content.Context
import android.content.Intent

fun Context.sendScanFile(filePath: String) = sendBroadcast(
    Intent(MediaScannerReceiver.Broadcast_SCAN_FILE).apply {
        putExtra(MediaScannerReceiver.FILE_PATH_ARG, filePath)
    }
)