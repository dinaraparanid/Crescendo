package com.paranid5.crescendo.domain.media_scanner

import android.content.Context
import android.content.Intent

fun Context.scanNextFile(nextFilePath: String) = sendBroadcast(
    Intent(MediaScannerReceiver.Broadcast_SCAN_NEXT_FILE).apply {
        putExtra(MediaScannerReceiver.NEXT_FILE_PATH_ARG, nextFilePath)
    }
)