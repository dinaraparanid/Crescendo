package com.paranid5.crescendo.core.media.media_scanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.core.common.uri.Path
import com.paranid5.crescendo.utils.extensions.getParcelableCompat
import com.paranid5.crescendo.utils.extensions.notNull

internal class MediaScannerReceiver : BroadcastReceiver() {
    companion object {
        private const val RECEIVER_LOCATION = "com.paranid5.crescendo.core.media.media_scanner"
        const val BROADCAST_SCAN_FILE = "$RECEIVER_LOCATION.SCAN_FILE"
        const val FILE_PATH_ARG = "next_file_path"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (intent.action != BROADCAST_SCAN_FILE) return

        val nextFilePath = intent.nextFilePathArg

        MediaScannerClient(context, nextFilePath).scan()
    }

    private inline val Intent.nextFilePathArg
        get() = getParcelableCompat(FILE_PATH_ARG, Path::class).notNull
}
