package com.paranid5.crescendo.services.video_cache_service.files

import android.util.Log
import arrow.core.Either
import com.paranid5.crescendo.domain.caching.CachingResult
import com.paranid5.crescendo.domain.caching.DownloadingStatus
import com.paranid5.crescendo.domain.ktor_client.DownloadingProgress
import com.paranid5.crescendo.domain.ktor_client.UrlWithFile
import com.paranid5.crescendo.domain.ktor_client.downloadFile
import com.paranid5.crescendo.domain.ktor_client.downloadFiles
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

private const val TAG = "MediaFileDownloader"

class MediaFileDownloader : KoinComponent {
    private val ktorClient by inject<HttpClient>()

    private val _videoDownloadProgressState by lazy {
        MutableStateFlow(DownloadingProgress(0, 0))
    }

    val videoDownloadProgressState by lazy {
        _videoDownloadProgressState.asStateFlow()
    }

    private val _downloadStatusState by lazy {
        MutableStateFlow<DownloadingStatus>(DownloadingStatus.None)
    }

    val downloadStatusState by lazy {
        _downloadStatusState.asStateFlow()
    }

    suspend fun downloadAudioFile(
        desiredFilename: String,
        mediaUrl: String,
        isAudio: Boolean
    ): CachingResult.DownloadResult {
        _downloadStatusState.update { DownloadingStatus.Downloading }

        val curVideoCacheFile = when (val storeFileRes =
            initMediaFile(
                desiredFilename = desiredFilename,
                isAudio = isAudio
            )
        ) {
            is Either.Left -> return storeFileRes.value
            is Either.Right -> storeFileRes.value
        }

        val statusCode = ktorClient.downloadFile(
            fileUrl = mediaUrl,
            storeFile = curVideoCacheFile,
            totalProgressState = _videoDownloadProgressState,
            downloadingState = _downloadStatusState,
        )

        return when (statusCode?.isSuccess()) {
            true -> CachingResult.DownloadResult.Success(listOf(curVideoCacheFile))
            false -> onError(statusCode, curVideoCacheFile)
            null -> onCancel(curVideoCacheFile)
        }
    }

    suspend fun downloadAudioAndVideoFiles(
        desiredFilename: String,
        audioUrl: String,
        videoUrl: String,
    ): CachingResult.DownloadResult {
        val mediaFilesInitResult = prepareMediaFilesForMP4Merging(desiredFilename)

        val (audioFileStore, videoFileStore) = when (mediaFilesInitResult) {
            is Either.Left -> return mediaFilesInitResult.value
            is Either.Right -> mediaFilesInitResult.value
        }

        val downloadStatus = ktorClient.downloadFiles(
            _downloadStatusState,
            _videoDownloadProgressState,
            UrlWithFile(audioUrl, audioFileStore), UrlWithFile(videoUrl, videoFileStore)
        )

        return when (downloadStatus) {
            DownloadingStatus.Downloaded ->
                CachingResult.DownloadResult.Success(listOf(audioFileStore, videoFileStore))

            DownloadingStatus.CanceledCurrent ->
                onCancel(audioFileStore, videoFileStore)

            DownloadingStatus.CanceledAll ->
                onCancel(audioFileStore, videoFileStore)

            is DownloadingStatus.Error ->
                onError(downloadStatus.status, audioFileStore, videoFileStore)

            DownloadingStatus.ConnectionLost ->
                onConnectionLost()

            else -> throw IllegalStateException("Illegal final downloading state")
        }
    }

    fun onCanceledCurrent() =
        _downloadStatusState.update { DownloadingStatus.CanceledCurrent }

    fun onCanceledAll() =
        _downloadStatusState.update { DownloadingStatus.CanceledAll }

    fun prepareForNewVideo() {
        _downloadStatusState.update { DownloadingStatus.None }
        _videoDownloadProgressState.update { DownloadingProgress(0, 0) }
    }

    fun resetDownloadStatus() =
        _downloadStatusState.update { it.afterReset }
}

private fun onError(
    statusCode: HttpStatusCode,
    vararg videoCacheFiles: File
): CachingResult.DownloadResult.Error {
    videoCacheFiles.forEach { Log.d(TAG, "File is deleted ${it.delete()}") }
    Log.d(TAG, "Downloading was interrupted by an error ${statusCode.value}")
    return CachingResult.DownloadResult.Error(statusCode)
}

private fun onCancel(vararg videoCacheFiles: File): CachingResult.Canceled {
    videoCacheFiles.forEach { Log.d(TAG, "File is deleted ${it.delete()}") }
    Log.d(TAG, "Downloading was canceled")
    return CachingResult.Canceled
}

private fun onConnectionLost(vararg videoCacheFiles: File): CachingResult.DownloadResult.ConnectionLostError {
    videoCacheFiles.forEach { Log.d(TAG, "File is deleted ${it.delete()}") }
    Log.d(TAG, "Connection was lost")
    return CachingResult.DownloadResult.ConnectionLostError
}

private inline val DownloadingStatus.afterReset
    get() = when (this) {
        DownloadingStatus.Downloading -> this
        else -> DownloadingStatus.None
    }