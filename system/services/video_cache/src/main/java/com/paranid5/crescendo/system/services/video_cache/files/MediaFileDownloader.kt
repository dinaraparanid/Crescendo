package com.paranid5.crescendo.system.services.video_cache.files

import android.util.Log
import com.paranid5.crescendo.core.common.caching.DownloadFilesStatus
import com.paranid5.crescendo.core.common.caching.DownloadingStatus
import com.paranid5.crescendo.core.media.caching.CachingResult
import com.paranid5.crescendo.core.media.files.MediaFile
import com.paranid5.crescendo.data.ktor.DownloadingProgress
import com.paranid5.crescendo.data.ktor.downloadFile
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.util.concurrent.atomic.AtomicLong

private const val TAG = "MediaFileDownloader"

internal class MediaFileDownloader : KoinComponent {
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

    suspend fun downloadFile(
        cacheFile: MediaFile,
        mediaUrl: String,
        progress: AtomicLong,
    ): CachingResult.DownloadResult {
        _downloadStatusState.update { DownloadingStatus.Downloading }

        val status = ktorClient.downloadFile(
            fileUrl = mediaUrl,
            storeFile = cacheFile,
            progress = progress,
            totalProgressState = _videoDownloadProgressState,
            downloadingState = _downloadStatusState,
        )

        return when (status) {
            is DownloadFilesStatus.Success ->
                CachingResult.DownloadResult.Success(cacheFile)

            is DownloadFilesStatus.Error ->
                onError()

            is DownloadFilesStatus.Canceled ->
                onCancel(cacheFile)
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

private fun onError(): CachingResult.DownloadResult.Error =
    CachingResult.DownloadResult.Error

private fun onCancel(vararg videoCacheFiles: File): CachingResult.Canceled {
    videoCacheFiles.forEach { Log.d(TAG, "File is deleted ${it.delete()}") }
    Log.d(TAG, "Downloading was canceled")
    return CachingResult.Canceled
}

private inline val DownloadingStatus.afterReset
    get() = when (this) {
        DownloadingStatus.Downloading -> this
        else -> DownloadingStatus.None
    }