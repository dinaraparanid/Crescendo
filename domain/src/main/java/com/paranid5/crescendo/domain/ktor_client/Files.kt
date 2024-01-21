package com.paranid5.crescendo.domain.ktor_client

import arrow.core.Either
import com.paranid5.crescendo.domain.caching.DownloadError
import com.paranid5.crescendo.domain.caching.DownloadingStatus
import io.ktor.client.HttpClient
import io.ktor.client.plugins.retry
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.utils.io.cancel
import io.ktor.utils.io.core.isNotEmpty
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.discardExact
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import java.io.File
import java.util.concurrent.atomic.AtomicLong

private const val TAG = "Ktor Client"

data class DownloadingProgress(val downloadedBytes: Long, val totalBytes: Long)

data class UrlWithFile(val fileUrl: String, val file: File)

private data class RequestWithFile(val request: HttpStatement, val file: File)

suspend inline fun HttpClient.getFileExt(fileUrl: String) =
    prepareRetryingGet(fileUrl).execute { response ->
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

suspend fun HttpClient.downloadFile(
    fileUrl: String,
    storeFile: File,
    downloadingState: MutableStateFlow<DownloadingStatus>,
    progressState: MutableStateFlow<DownloadingProgress>? = null
): HttpStatusCode? {
    val progress = AtomicLong()

    val status = prepareRetryingGet(fileUrl)
        .downloadFileFlow(storeFile, progress, downloadingState, progressState) { response ->
            response.contentLength()!!
        }
        .first { status -> status.isRight() }
        .getOrNull()

    downloadingState.update { downloadingState.finishedValue }
    return status
}

private inline fun HttpStatement.downloadFileFlow(
    storeFile: File,
    progress: AtomicLong,
    downloadingState: MutableStateFlow<DownloadingStatus>,
    progressState: MutableStateFlow<DownloadingProgress>?,
    crossinline totalBytes: (response: HttpResponse) -> Long
) = flow {
    while (true)
        emit(downloadFileResult(storeFile, progress, downloadingState, progressState, totalBytes))
}

private suspend inline fun HttpStatement.downloadFileResult(
    storeFile: File,
    progress: AtomicLong,
    downloadingState: MutableStateFlow<DownloadingStatus>,
    progressState: MutableStateFlow<DownloadingProgress>?,
    crossinline totalBytes: (response: HttpResponse) -> Long
) = Either.catch {
    execute { response ->
        if (!response.status.isSuccess()) {
            downloadingState.update { DownloadingStatus.ERR }
            return@execute response.status
        }

        response.downloadFileImpl(
            downloadingState = downloadingState,
            progressState = progressState,
            storeFile = storeFile,
            totalBytes = totalBytes(response),
            progress = progress
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
    val getRequests = files.getRequests()

    getRequests.firstFailure()?.let { err ->
        errorState.update { DownloadError(err.value, err.description) }
        downloadingState.update { DownloadingStatus.ERR }
        return@coroutineScope err
    }

    val bytesPerFiles = getRequests.bytesPerFile()
    val totalBytes = bytesPerFiles.sum()
    getRequests.downloadFilesUntilError(downloadingState, progressState, errorState, totalBytes)

    downloadingState
        .updateAndGet { downloadingState.finishedValue }
        .httpStatusCode(errorState)
}

private suspend inline fun HttpResponse.downloadFileImpl(
    downloadingState: MutableStateFlow<DownloadingStatus>,
    progressState: MutableStateFlow<DownloadingProgress>?,
    storeFile: File,
    totalBytes: Long,
    progress: AtomicLong
): HttpStatusCode? {
    val channel = bodyAsChannel().apply { discardExact(progress.get()) }

    while (!channel.isClosedForRead && downloadingState.value == DownloadingStatus.DOWNLOADING) {
        val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())

        while (packet.isNotEmpty && downloadingState.value == DownloadingStatus.DOWNLOADING) {
            val bytes = packet.readBytes()
            progress.addAndGet(bytes.size.toLong())
            storeFile.appendBytes(bytes)

            progressState?.update { (progress, _) ->
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

context(HttpClient)
private suspend inline fun Array<out UrlWithFile>.getRequests() =
    map { (fileUrl, storeFile) ->
        RequestWithFile(prepareRetryingGet(fileUrl), storeFile)
    }

private suspend inline fun List<RequestWithFile>.firstFailure() =
    map { (request, _) -> request.execute { it.status } }
        .firstOrNull { !it.isSuccess() }

private suspend inline fun List<RequestWithFile>.bytesPerFile() =
    map { (request, _) -> request.execute { response -> response.contentLength()!! } }

private suspend inline fun List<RequestWithFile>.downloadFilesUntilError(
    downloadingState: MutableStateFlow<DownloadingStatus>,
    progressState: MutableStateFlow<DownloadingProgress>?,
    errorState: MutableStateFlow<DownloadError>,
    totalBytes: Long
) = firstOrNull { (request, storeFile) ->
    val progress = AtomicLong()

    val status = request
        .downloadFileFlow(storeFile, progress, downloadingState, progressState) { totalBytes }
        .first { it.isRight() }
        .getOrNull()

    val isOk = status?.isSuccess() == true

    if (!isOk && status != null)
        errorState.update { DownloadError(status.value, status.description) }

    !isOk
}

suspend inline fun HttpClient.prepareRetryingGet(url: String) =
    prepareGet(url) {
        retry {
            maxRetries = -1

            retryIf { _, response ->
                !response.status.isSuccess()
            }
        }
    }

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