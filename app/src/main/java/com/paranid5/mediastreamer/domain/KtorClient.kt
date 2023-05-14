package com.paranid5.mediastreamer.domain

import android.util.Log
import com.paranid5.mediastreamer.domain.video_cash_service.CashingStatus
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.utils.io.core.isNotEmpty
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import java.io.File

private const val TAG = "Ktor Client"

fun KtorClient() = HttpClient(Android) {
    install(Logging) {
        logger = Logger.ANDROID
        level = LogLevel.ALL
    }
}

suspend inline fun HttpClient.getFileExt(fileUrl: String) =
    prepareGet(fileUrl).execute { response ->
        response.headers["content-type"]!!.split("/")[1]
    }

/**
 * Downloads youtube file by url
 * @param fileUrl url from youtube to media file
 * @param storeFile file in which all content will be stored
 * @param progressState channel that indicates downloading progress
 * with both current progress in bytes and total file's size
 * @return HTTP status code on this request or null, if cashing was canceled by user
 */

internal suspend inline fun HttpClient.downloadFile(
    fileUrl: String,
    storeFile: File,
    progressState: MutableStateFlow<Pair<Long, Long>>? = null,
    cashingStatusState: MutableStateFlow<CashingStatus>
): HttpStatusCode? {
    Log.d(TAG, "Downloading $fileUrl")

    val status = prepareGet(fileUrl).execute { response ->
        val status = response.status

        if (!status.isSuccess()) {
            cashingStatusState.update { CashingStatus.ERR }
            return@execute status
        }

        val channel = response.bodyAsChannel()
        var progress = 0L

        while (!channel.isClosedForRead && cashingStatusState.value == CashingStatus.CASHING) {
            val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())

            while (packet.isNotEmpty && cashingStatusState.value == CashingStatus.CASHING) {
                val bytes = packet.readBytes()
                storeFile.appendBytes(bytes)

                progress += bytes.size
                progressState?.update { progress to response.contentLength()!! }

                Log.d(
                    TAG,
                    "Read ${bytes.size} bytes from ${response.contentLength()}. Total progress: $progress bytes"
                )
            }
        }

        val cashingStatus = cashingStatusState.updateAndGet {
            when (it) {
                CashingStatus.CANCELED -> it
                else -> CashingStatus.CASHED
            }
        }

        if (cashingStatus == CashingStatus.CANCELED) null else status
    }

    Log.d(TAG, "Done, status: ${status?.value}")
    return status
}