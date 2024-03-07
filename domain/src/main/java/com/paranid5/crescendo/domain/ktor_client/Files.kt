package com.paranid5.crescendo.domain.ktor_client

import android.util.Log
import arrow.core.Either
import arrow.core.raise.nullable
import com.paranid5.crescendo.domain.caching.DownloadingStatus
import com.paranid5.crescendo.domain.caching.isCanceled
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.utils.io.cancel
import io.ktor.utils.io.core.isNotEmpty
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.util.concurrent.atomic.AtomicLong

private const val TAG = "Ktor Client"

private const val NEXT_PACKET_TIMEOUT = 3000L

data class DownloadingProgress(val downloadedBytes: Long, val totalBytes: Long)

data class UrlWithFile(val fileUrl: String, val file: File)

/**
 * Downloads file from given url until file is downloaded or task is canceled
 * @param fileUrl url from youtube to media file
 * @param storeFile file in which all content will be stored
 * @param totalProgressState channel that indicates downloading progress
 * with both current progress in bytes and total file's size
 * @param downloadingState current [DownloadingStatus].
 * Notifies, if user has canceled the task
 * @return HTTP status code on this request or null, if downloading was canceled by user
 */

suspend fun HttpClient.downloadFile(
    fileUrl: String,
    storeFile: File,
    downloadingState: MutableStateFlow<DownloadingStatus>,
    totalProgressState: MutableStateFlow<DownloadingProgress>? = null
): HttpStatusCode? {
    val progress = AtomicLong()

    val status = downloadFileResult(
        fileUrl = fileUrl,
        storeFile = storeFile,
        fileProgress = progress,
        downloadingState = downloadingState,
        totalProgressState = totalProgressState,
        totalBytes = { it.totalBytes }
    ).getOrNull()

    downloadingState.updatedToFinishedValue()
    Log.d(TAG, "Done, status: $status")
    return status
}

context(HttpClient)
private suspend inline fun downloadFileResult(
    fileUrl: String,
    storeFile: File,
    fileProgress: AtomicLong,
    downloadingState: MutableStateFlow<DownloadingStatus>,
    totalProgressState: MutableStateFlow<DownloadingProgress>?,
    crossinline totalBytes: (response: HttpResponse) -> Long
) = Either.catch {
    if (downloadingState.value.isCanceled)
        return@catch null

    prepareFileGet(url = fileUrl, progress = fileProgress.get()).execute { response ->
        if (!response.status.isSuccess())
            return@execute response.status

        response.downloadFileImpl(
            downloadingState = downloadingState,
            totalProgressState = totalProgressState,
            storeFile = storeFile,
            totalBytes = totalBytes(response),
            fileProgress = fileProgress
        )
    }
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

suspend fun HttpClient.downloadFiles(
    downloadingState: MutableStateFlow<DownloadingStatus>,
    progressState: MutableStateFlow<DownloadingProgress>? = null,
    vararg files: UrlWithFile
): DownloadingStatus = coroutineScope {
    downloadFilesUntilError(downloadingState, progressState, *files)
    downloadingState.updatedToFinishedValue()
}

private suspend inline fun HttpResponse.downloadFileImpl(
    downloadingState: MutableStateFlow<DownloadingStatus>,
    totalProgressState: MutableStateFlow<DownloadingProgress>?,
    storeFile: File,
    totalBytes: Long,
    fileProgress: AtomicLong
): HttpStatusCode? {
    val channel = bodyAsChannel()

    while (!channel.isClosedForRead && downloadingState.value == DownloadingStatus.Downloading) {
        val packet = withTimeoutOrNull(NEXT_PACKET_TIMEOUT) {
            channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
        } ?: continue

        while (packet.isNotEmpty && downloadingState.value == DownloadingStatus.Downloading) {
            val bytes = packet.readBytes()
            fileProgress.addAndGet(bytes.size.toLong())
            storeFile.appendBytes(bytes)

            totalProgressState?.update { (progress, _) ->
                DownloadingProgress(progress + bytes.size, totalBytes)
            }
        }
    }

    channel.cancel()

    return when (downloadingState.finishedValue) {
        DownloadingStatus.CanceledAll, DownloadingStatus.CanceledCurrent -> null
        else -> status
    }
}

context(HttpClient)
private suspend fun downloadFilesUntilError(
    downloadingState: MutableStateFlow<DownloadingStatus>,
    totalProgressState: MutableStateFlow<DownloadingProgress>?,
    vararg files: UrlWithFile
): UrlWithFile? {
    downloadingState.update { DownloadingStatus.Downloading }

    suspend fun downloadSingleFile(
        fileUrl: String,
        storeFile: File,
        totalBytes: Long
    ): Boolean {
        val progress = AtomicLong()

        val status = downloadFileResult(
            fileUrl = fileUrl,
            storeFile = storeFile,
            fileProgress = progress,
            downloadingState = downloadingState,
            totalProgressState = totalProgressState,
            totalBytes = { totalBytes }
        ).getOrNull()

        return status?.isSuccess() != false
    }

    return nullable {
        val totalBytes = files
            .map { contentLength(it.fileUrl) }
            .bindAll()
            .sum()

        files.firstOrNull { (fileUrl, storeFile) ->
            !downloadSingleFile(fileUrl, storeFile, totalBytes)
        }.bind()
    }
}

context(HttpClient)
private suspend inline fun prepareFileGet(
    url: String,
    progress: Long,
) = prepareGet(url) {
    header(HttpHeaders.Range, "bytes=$progress-")
}

context(HttpClient)
private suspend inline fun contentLength(url: String) =
    prepareGet(url).execute { if (it.status.isSuccess()) it.contentLength() else null }

private inline val HttpResponse.totalBytes
    get() = headers[HttpHeaders.ContentRange]
        ?.split("/")
        ?.getOrNull(1)
        ?.toLongOrNull()
        ?: contentLength()
        ?: 0L

private fun MutableStateFlow<DownloadingStatus>.updatedToFinishedValue() =
    updateAndGet { it.finished }

private inline val StateFlow<DownloadingStatus>.finishedValue
    get() = value.finished

private inline val DownloadingStatus.finished
    get() = when (this) {
        DownloadingStatus.Downloading -> DownloadingStatus.Downloaded
        else -> this
    }