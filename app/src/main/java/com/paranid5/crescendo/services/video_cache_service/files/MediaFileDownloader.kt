package com.paranid5.crescendo.services.video_cache_service.files

import android.util.Log
import arrow.core.Either
import com.paranid5.crescendo.domain.caching.CachingResult
import com.paranid5.crescendo.domain.caching.DownloadError
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
        MutableStateFlow(DownloadingStatus.NONE)
    }

    val downloadStatusState by lazy {
        _downloadStatusState.asStateFlow()
    }

    private val _downloadErrorState by lazy {
        MutableStateFlow(DownloadError())
    }

    val downloadErrorState by lazy {
        _downloadErrorState.asStateFlow()
    }

    fun onDownloadError(errorCode: Int, errorDescription: String) =
        _downloadErrorState.update { DownloadError(errorCode, errorDescription) }

    suspend fun downloadAudioFile(
        desiredFilename: String,
        mediaUrl: String,
        isAudio: Boolean
    ): CachingResult.DownloadResult {
        _downloadStatusState.update { DownloadingStatus.DOWNLOADING }

        val curVideoCashFile = when (val storeFileRes =
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
            storeFile = curVideoCashFile,
            progressState = _videoDownloadProgressState,
            downloadingState = _downloadStatusState,
        )

        return when (statusCode?.isSuccess()) {
            true -> CachingResult.DownloadResult.Success(listOf(curVideoCashFile))
            false -> onError(statusCode, curVideoCashFile)
            null -> onCancel(curVideoCashFile)
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

        val statusCode = ktorClient.downloadFiles(
            _downloadStatusState,
            _downloadErrorState,
            _videoDownloadProgressState,
            UrlWithFile(audioUrl, audioFileStore), UrlWithFile(videoUrl, videoFileStore)
        )

        return when (statusCode?.isSuccess()) {
            true -> CachingResult.DownloadResult.Success(listOf(audioFileStore, videoFileStore))
            false -> onError(statusCode, audioFileStore, videoFileStore)
            null -> onCancel(audioFileStore, videoFileStore)
        }
    }

    internal fun onCanceledCurrent() =
        _downloadStatusState.update { DownloadingStatus.CANCELED_CUR }

    internal fun onCanceledAll() =
        _downloadStatusState.update { DownloadingStatus.CANCELED_ALL }

    internal fun prepareForNewVideo() {
        _downloadStatusState.update { DownloadingStatus.DOWNLOADING }
        _videoDownloadProgressState.update { DownloadingProgress(0, 0) }
        _downloadErrorState.update { DownloadError() }
    }

    internal fun resetDownloadStatus() =
        _downloadStatusState.update { DownloadingStatus.DOWNLOADING }

    private fun onError(
        statusCode: HttpStatusCode,
        vararg videoCacheFiles: File
    ): CachingResult.DownloadResult.Error {
        _downloadErrorState.update { DownloadError(statusCode.value, statusCode.description) }
        videoCacheFiles.forEach { Log.d(TAG, "File is deleted ${it.delete()}") }
        Log.d(TAG, "Caching was interrupted by an error ${statusCode.value}")
        return CachingResult.DownloadResult.Error(statusCode)
    }

    private fun onCancel(vararg videoCacheFiles: File): CachingResult.Canceled {
        videoCacheFiles.forEach { Log.d(TAG, "File is deleted ${it.delete()}") }
        Log.d(TAG, "Caching was canceled")
        return CachingResult.Canceled
    }
}