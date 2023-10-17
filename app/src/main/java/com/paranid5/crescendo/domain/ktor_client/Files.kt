package com.paranid5.crescendo.domain.ktor_client

import android.util.Log
import com.paranid5.crescendo.domain.services.video_cash_service.DownloadingStatus
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.utils.io.core.isNotEmpty
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.discardExact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "Ktor Client"

suspend inline fun HttpClient.getFileExt(fileUrl: String) =
    prepareGet(fileUrl).execute { response ->
        response.headers["content-type"]!!.split("/")[1]
    }

/**
 * Downloads file from given url until file is downloaded or task is canceled
 * @param fileUrl url from youtube to media file
 * @param storeFile file in which all content will be stored
 * @param progressState channel that indicates downloading progress
 * with both current progress in bytes and total file's size
 * @param downloadingState current [DownloadingStatus].
 * Notifies, if user has canceled the task
 * @return HTTP status code on this request or null, if downloading was canceled by user
 */

internal suspend inline fun HttpClient.downloadFile(
    fileUrl: String,
    storeFile: File,
    progressState: MutableStateFlow<Pair<Long, Long>>? = null,
    downloadingState: MutableStateFlow<DownloadingStatus>
): HttpStatusCode? {
    Log.d(TAG, "Downloading $fileUrl")

    var progress = 0L
    var statusRes: Result<HttpStatusCode?>

    do {
        statusRes = runCatching {
            prepareGet(fileUrl).execute { response ->
                val status = response.status

                if (!status.isSuccess()) {
                    downloadingState.update { DownloadingStatus.ERR }
                    return@execute status
                }

                val channel = response
                    .bodyAsChannel()
                    .apply { discardExact(progress) }

                while (!channel.isClosedForRead && downloadingState.value == DownloadingStatus.DOWNLOADING) {
                    val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())

                    while (packet.isNotEmpty && downloadingState.value == DownloadingStatus.DOWNLOADING) {
                        val bytes = packet.readBytes()
                        storeFile.appendBytes(bytes)

                        progress += bytes.size
                        progressState?.update { progress to response.contentLength()!! }

                        Log.d(
                            TAG,
                            "Read ${bytes.size} bytes from ${response.contentLength()}. " +
                                    "Total progress: $progress bytes"
                        )
                    }
                }

                val downloadingStatus = downloadingState.updateAndGet {
                    when (it) {
                        DownloadingStatus.CANCELED -> it
                        else -> DownloadingStatus.DOWNLOADED
                    }
                }

                if (downloadingStatus == DownloadingStatus.CANCELED) null else status
            }
        }
    } while (statusRes.isFailure)

    val status = statusRes.getOrNull()
    Log.d(TAG, "Done, status: ${status?.value}")
    return status
}

/**
 * Downloads multiple files simultaneously by the given urls
 * until all files are downloaded or task is canceled
 * @param files file urls and their store files
 * @param progressState channel that indicates downloading progress
 * with both current progress in bytes and total files' size
 * @param downloadingState current [DownloadingStatus].
 * Notifies, if user has canceled the task
 * @return HTTP status code on this request or null, if downloading was canceled by user
 */

internal suspend inline fun HttpClient.downloadFiles(
    files: Array<Pair<String, File>>,
    progressState: MutableStateFlow<Pair<Long, Long>>? = null,
    downloadingState: MutableStateFlow<DownloadingStatus>
): HttpStatusCode? = coroutineScope {
    Log.d(TAG, "Downloading multiple files")

    val getRequests = files.map { (fileUrl, storeFile) -> prepareGet(fileUrl) to storeFile }

    getRequests
        .map { (request, _) -> request.execute { it.status } }
        .firstOrNull { !it.isSuccess() }
        ?.let { return@coroutineScope it }

    val totalBytes = getRequests.fold(0L) { acc, (request, _) ->
        acc + request.execute { response -> response.contentLength()!! }
    }

    getRequests.map { (request, storeFile) ->
        launch(Dispatchers.IO) {
            request.execute { response ->
                val channel = response.bodyAsChannel()

                while (!channel.isClosedForRead && downloadingState.value == DownloadingStatus.DOWNLOADING) {
                    val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())

                    while (packet.isNotEmpty && downloadingState.value == DownloadingStatus.DOWNLOADING) {
                        val bytes = packet.readBytes()
                        storeFile.appendBytes(bytes)

                        progressState?.update { (progress, _) ->
                            progress + bytes.size to totalBytes
                        }

                        Log.d(
                            TAG,
                            "Read ${bytes.size} bytes from $totalBytes. " +
                                    "Total progress: ${progressState?.value?.first} bytes"
                        )
                    }
                }
            }
        }
    }.joinAll()

    val downloadingStatus = downloadingState.updateAndGet {
        when (it) {
            DownloadingStatus.CANCELED -> it
            else -> DownloadingStatus.DOWNLOADED
        }
    }

    val status = when (downloadingStatus) {
        DownloadingStatus.CANCELED -> null
        else -> HttpStatusCode.OK
    }

    Log.d(TAG, "Done, status: ${status?.value}")
    status
}
