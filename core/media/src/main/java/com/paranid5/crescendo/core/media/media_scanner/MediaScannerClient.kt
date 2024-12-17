package com.paranid5.crescendo.core.media.media_scanner

import android.content.Context
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.MediaScannerConnectionClient
import android.net.Uri
import com.paranid5.crescendo.core.common.uri.Path

class MediaScannerClient(context: Context, private val filePath: Path) :
    MediaScannerConnectionClient {
    private val connection = MediaScannerConnection(context.applicationContext, this)
    fun scan() = connection.connect()

    override fun onMediaScannerConnected() = connection.scanFile(filePath.toString(), null)
    override fun onScanCompleted(path: String?, uri: Uri?) = connection.disconnect()
}