package com.paranid5.crescendo.domain.media_scanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MediaScannerReceiver : BroadcastReceiver() {
    companion object {
        private val TAG = MediaScannerReceiver::class.simpleName!!
        private const val RECEIVER_LOCATION = "com.paranid5.crescendo.domain.media_scanner"
        const val Broadcast_SCAN_FILE = "$RECEIVER_LOCATION.SCAN_FILE"
        const val FILE_PATH_ARG = "next_file_path"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val nextFilePath = intent!!.nextFilePathArg
        Log.d(TAG, "Scanning file: $nextFilePath")
        MediaScannerClient(context!!, nextFilePath).scan()
    }
}

private inline val Intent.nextFilePathArg
    get() = getStringExtra(MediaScannerReceiver.FILE_PATH_ARG)!!