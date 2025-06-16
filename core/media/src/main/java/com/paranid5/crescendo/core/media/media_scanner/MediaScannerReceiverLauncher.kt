package com.paranid5.crescendo.core.media.media_scanner

import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.core.common.uri.Path
import com.paranid5.crescendo.utils.extensions.sendAppBroadcast

fun Context.sendScanFile(filePath: Path) = sendAppBroadcast(
    Intent(applicationContext, MediaScannerReceiver::class.java)
        .setAction(MediaScannerReceiver.BROADCAST_SCAN_FILE)
        .putExtra(MediaScannerReceiver.FILE_PATH_ARG, filePath)
)
