package com.paranid5.mediastreamer.domain.media_scanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MediaScannerReceiver : BroadcastReceiver() {
    companion object {
        private const val ReceiverLocation = "com.paranid5.mediastreamer.domain.media_scanner"
        internal const val Broadcast_SCAN_NEXT_FILE = "$ReceiverLocation.SCAN_NEXT_FILE"

        internal const val NEXT_FILE_PATH_ARG = "next_file_path"

        internal inline val Intent.mNextFilePathArg
            get() = getStringExtra(NEXT_FILE_PATH_ARG)!!
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val nextFilePath = intent!!.mNextFilePathArg
        MediaScannerClient(context!!, nextFilePath).scan()
    }
}