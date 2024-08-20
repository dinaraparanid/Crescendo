package com.paranid5.crescendo.data.ktor

import android.util.Log
import arrow.core.Either
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.common.caching.DownloadFilesStatus
import com.paranid5.crescendo.core.common.caching.DownloadingStatus
import com.paranid5.crescendo.core.common.caching.isCanceled
import com.paranid5.crescendo.utils.identity
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.utils.io.cancel
import io.ktor.utils.io.core.isNotEmpty
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.util.concurrent.atomic.AtomicLong

private const val TAG = "KtorClient"

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
    progress: AtomicLong,
    downloadingState: MutableStateFlow<DownloadingStatus>,
    totalProgressState: MutableStateFlow<DownloadingProgress>? = null
): DownloadFilesStatus {
    val status = downloadFileResult(
        fileUrl = fileUrl,
        storeFile = storeFile,
        fileProgress = progress,
        downloadingState = downloadingState,
        totalProgressState = totalProgressState,
        totalBytes = { it.totalBytes },
    )

    if (status is DownloadFilesStatus.Success)
        downloadingState.updatedToFinished()

    Log.d(TAG, "Done, status: $status")
    return status
}

context(HttpClient)
private suspend fun downloadFileResult(
    fileUrl: String,
    storeFile: File,
    fileProgress: AtomicLong,
    downloadingState: MutableStateFlow<DownloadingStatus>,
    totalProgressState: MutableStateFlow<DownloadingProgress>?,
    totalBytes: (response: HttpResponse) -> Long
): DownloadFilesStatus {
    suspend fun impl() = Either.catch {
        if (downloadingState.value.isCanceled)
            return@catch DownloadFilesStatus.Canceled

        prepareFileGet(url = fileUrl, progress = fileProgress.get()).execute { response ->
            if (response.status.isSuccess().not())
                return@execute DownloadFilesStatus.Error

            response.downloadFileImpl(
                downloadingState = downloadingState,
                totalProgressState = totalProgressState,
                storeFile = storeFile,
                totalBytes = totalBytes(response),
                fileProgress = fileProgress,
            )
        }
    }

    return impl().fold(
        ifLeft = { DownloadFilesStatus.Error },
        ifRight = ::identity,
    )
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
): DownloadFilesStatus = coroutineScope {
    downloadFilesUntilError(downloadingState, progressState, *files).also {
        if (it is DownloadFilesStatus.Success)
            downloadingState.updatedToFinished()
    }
}

private suspend inline fun HttpResponse.downloadFileImpl(
    downloadingState: MutableStateFlow<DownloadingStatus>,
    totalProgressState: MutableStateFlow<DownloadingProgress>?,
    storeFile: File,
    totalBytes: Long,
    fileProgress: AtomicLong,
): DownloadFilesStatus {
    val channel = bodyAsChannel()

    while (channel.isClosedForRead.not() && downloadingState.value == DownloadingStatus.Downloading) {
        val packet = withTimeoutOrNull(NEXT_PACKET_TIMEOUT) {
            channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
        } ?: throw Exception("Packet timeout")

        while (packet.isNotEmpty && downloadingState.value == DownloadingStatus.Downloading) {
            val bytes = packet.readBytes()
            fileProgress.addAndGet(bytes.size.toLong())
            withContext(Dispatchers.IO) { storeFile.appendBytes(bytes) }

            totalProgressState?.update { (progress, _) ->
                DownloadingProgress(progress + bytes.size, totalBytes)
            }
        }
    }

    channel.cancel()

    return when (downloadingState.finishedValue) {
        DownloadingStatus.CanceledAll, DownloadingStatus.CanceledCurrent -> DownloadFilesStatus.Canceled
        DownloadingStatus.Downloaded -> DownloadFilesStatus.Success
        else -> DownloadFilesStatus.Error
    }
}

context(HttpClient)
private suspend inline fun downloadFilesUntilError(
    downloadingState: MutableStateFlow<DownloadingStatus>,
    totalProgressState: MutableStateFlow<DownloadingProgress>?,
    vararg files: UrlWithFile,
): DownloadFilesStatus {
    downloadingState.update { DownloadingStatus.Downloading }

    val totalBytes = nullable {
        files
            .map { contentLength(it.fileUrl) }
            .bindAll()
            .sum()
    } ?: return DownloadFilesStatus.Error

    return files
        .asFlow()
        .map { (fileUrl, storeFile) ->
            downloadFileResult(
                fileUrl = fileUrl,
                storeFile = storeFile,
                fileProgress = AtomicLong(),
                downloadingState = downloadingState,
                totalProgressState = totalProgressState,
                totalBytes = { totalBytes },
            )
        }
        .firstOrNull { it !is DownloadFilesStatus.Success }
        ?: DownloadFilesStatus.Success
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

private fun MutableStateFlow<DownloadingStatus>.updatedToFinished() =
    updateAndGet { it.finished }

private inline val StateFlow<DownloadingStatus>.finishedValue
    get() = value.finished

private inline val DownloadingStatus.finished
    get() = when (this) {
        DownloadingStatus.Downloading -> DownloadingStatus.Downloaded
        else -> this
    }