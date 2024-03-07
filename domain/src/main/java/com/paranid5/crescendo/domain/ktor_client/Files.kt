package com.paranid5.crescendo.domain.ktor_client

import android.util.Log
import arrow.core.Either
import com.paranid5.crescendo.domain.caching.DownloadError
import com.paranid5.crescendo.domain.caching.DownloadingStatus
import com.paranid5.crescendo.domain.caching.isCanceled
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.utils.io.cancel
import io.ktor.utils.io.core.isNotEmpty
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.util.concurrent.atomic.AtomicLong

private const val TAG = "Ktor Client"

private const val NEXT_PACKET_TIMEOUT = 3000L

private const val RETRY_DELAY = 5000L

data class DownloadingProgress(val downloadedBytes: Long, val totalBytes: Long)

data class UrlWithFile(val fileUrl: String, val file: File)

private data class RequestWithFile(val request: HttpStatement, val file: File)

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

    val status = downloadFileFlow(
        fileUrl = fileUrl,
        storeFile = storeFile,
        fileProgress = progress,
        downloadingState = downloadingState,
        totalProgressState = totalProgressState,
        totalBytes = { it.totalBytes }
    )
        .first { it.isNotFailure() }
        .getOrNull()

    downloadingState.update { downloadingState.finishedValue }
    println("Done, status: $status")
    return status
}

private inline fun HttpClient.downloadFileFlow(
    fileUrl: String,
    storeFile: File,
    fileProgress: AtomicLong,
    downloadingState: MutableStateFlow<DownloadingStatus>,
    totalProgressState: MutableStateFlow<DownloadingProgress>?,
    crossinline totalBytes: (response: HttpResponse) -> Long
) = flow {
    while (true)
        emit(
            downloadFileResult(
                fileUrl = fileUrl,
                storeFile = storeFile,
                fileProgress = fileProgress,
                downloadingState = downloadingState,
                totalProgressState = totalProgressState,
                totalBytes = totalBytes
            )
        )
}

private suspend inline fun HttpClient.downloadFileResult(
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
        if (!response.status.isSuccess()) {
            Log.d(TAG, "ERROR: ${response.status}")
            return@execute response.status
        }

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
    errorState: MutableStateFlow<DownloadError>,
    progressState: MutableStateFlow<DownloadingProgress>? = null,
    vararg files: UrlWithFile
): HttpStatusCode? = coroutineScope {
    downloadFilesUntilError(downloadingState, progressState, errorState, *files)

    downloadingState
        .updateAndGet { downloadingState.finishedValue }
        .httpStatusCode(errorState)
}

private suspend inline fun HttpResponse.downloadFileImpl(
    downloadingState: MutableStateFlow<DownloadingStatus>,
    totalProgressState: MutableStateFlow<DownloadingProgress>?,
    storeFile: File,
    totalBytes: Long,
    fileProgress: AtomicLong
): HttpStatusCode? {
    val channel = bodyAsChannel()

    while (!channel.isClosedForRead && downloadingState.value == DownloadingStatus.DOWNLOADING) {
        val packet = withTimeoutOrNull(NEXT_PACKET_TIMEOUT) {
            channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
        } ?: continue

        while (packet.isNotEmpty && downloadingState.value == DownloadingStatus.DOWNLOADING) {
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
        DownloadingStatus.CANCELED_ALL, DownloadingStatus.CANCELED_CUR -> null
        else -> status
    }
}

private suspend inline fun List<RequestWithFile>.firstFailure() =
    map { (request, _) -> request.execute { it.status } }
        .firstOrNull { !it.isSuccess() }

private suspend inline fun List<RequestWithFile>.bytesPerFile() =
    map { (request, _) -> request.execute { response -> response.contentLength()!! } }

private suspend inline fun HttpClient.downloadFilesUntilError(
    downloadingState: MutableStateFlow<DownloadingStatus>,
    totalProgressState: MutableStateFlow<DownloadingProgress>?,
    errorState: MutableStateFlow<DownloadError>,
    vararg files: UrlWithFile
) = files.firstOrNull { (fileUrl, storeFile) ->
    val progress = AtomicLong()

    val status = downloadFileFlow(
        fileUrl = fileUrl,
        storeFile = storeFile,
        fileProgress = progress,
        downloadingState = downloadingState,
        totalProgressState = totalProgressState,
    ) { it.totalBytes } // TODO: get full content len
        .first { it.isNotFailure() }
        .getOrNull()

    val isOk = status?.isSuccess() == true

    if (!isOk && status != null)
        errorState.update { DownloadError(status.value, status.description) }

    !isOk
}

private suspend inline fun HttpClient.prepareFileGet(
    url: String,
    progress: Long,
) = prepareGet(url) {
    header(HttpHeaders.Range, "bytes=$progress-")
}

private inline val HttpResponse.totalBytes
    get() = headers[HttpHeaders.ContentRange]
        ?.split("/")
        ?.getOrNull(1)
        ?.toLongOrNull()
        ?: contentLength()
        ?: 0L

private inline val MutableStateFlow<DownloadingStatus>.finishedValue
    get() = when (value) {
        DownloadingStatus.DOWNLOADING -> DownloadingStatus.DOWNLOADED
        else -> value
    }

private fun DownloadingStatus.httpStatusCode(errorState: MutableStateFlow<DownloadError>) =
    when (this) {
        DownloadingStatus.CANCELED_CUR -> null
        DownloadingStatus.CANCELED_ALL -> null
        DownloadingStatus.DOWNLOADED -> HttpStatusCode.OK
        else -> HttpStatusCode(errorState.value.errorCode, errorState.value.errorDescription)
    }

private suspend inline fun Either<Throwable, HttpStatusCode?>.isNotFailure() =
    isRight { it?.isSuccess() != false }.also { if (!it) delay(RETRY_DELAY) }