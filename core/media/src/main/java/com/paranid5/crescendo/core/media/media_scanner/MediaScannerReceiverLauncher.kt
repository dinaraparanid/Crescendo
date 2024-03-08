package com.paranid5.crescendo.core.media.media_scanner

import android.content.Context
import android.content.Intent

fun Context.sendScanFile(filePath: String) = sendBroadcast(
    Intent(applicationContext, MediaScannerReceiver::class.java)
        .setAction(MediaScannerReceiver.Broadcast_SCAN_FILE)
        .putExtra(MediaScannerReceiver.FILE_PATH_ARG, filePath)
)