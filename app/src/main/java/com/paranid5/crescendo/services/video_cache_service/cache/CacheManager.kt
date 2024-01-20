package com.paranid5.crescendo.services.video_cache_service.cache

import android.util.Log
import com.paranid5.crescendo.domain.caching.CachingStatus
import com.paranid5.crescendo.domain.caching.VideoCacheData
import com.paranid5.crescendo.domain.metadata.VideoMetadata
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

private const val TAG = "CacheManager"

class CacheManager {
    private val _videoCacheFlow by lazy {
        MutableSharedFlow<VideoCacheData>()
    }

    val videoCacheFlow by lazy {
        _videoCacheFlow.asSharedFlow()
    }

    private val _videoCacheQueueLenState by lazy {
        MutableStateFlow(0)
    }

    val videoCacheQueueLenState by lazy {
        _videoCacheQueueLenState.asStateFlow()
    }

    private val _cachingStatusState by lazy {
        MutableStateFlow(CachingStatus.NONE)
    }

    val cachingStatusState by lazy {
        _cachingStatusState.asStateFlow()
    }

    private val _currentVideoMetadataState by lazy {
        MutableStateFlow(VideoMetadata())
    }

    val currentVideoMetadataState by lazy {
        _currentVideoMetadataState.asStateFlow()
    }

    internal fun resetVideoMetadata(videoMetadata: VideoMetadata) =
        _currentVideoMetadataState.update { videoMetadata }

    suspend fun cacheNewVideo(videoCacheData: VideoCacheData) {
        _videoCacheQueueLenState.update { it + 1 }
        _videoCacheFlow.emit(videoCacheData)
    }

    internal fun onConversionStarted() =
        _cachingStatusState.update { CachingStatus.CONVERTING }

    internal fun onCanceledCurrent() {
        decrementQueueLen()
        _cachingStatusState.update { CachingStatus.CANCELED_CUR }
    }

    internal fun onCanceledAll() {
        _videoCacheQueueLenState.update { 0 }
        _cachingStatusState.update { CachingStatus.CANCELED_ALL }
    }

    internal fun onConverted() {
        decrementQueueLen()
        _cachingStatusState.update { CachingStatus.CONVERTED }
    }

    internal fun onDownloadFailed() {
        decrementQueueLen()
        _cachingStatusState.update { CachingStatus.ERR }
        Log.d(TAG, "Downloading was interrupted by an error")
    }

    internal fun onCachingError(vararg videoCacheFiles: File) {
        decrementQueueLen()
        _cachingStatusState.update { CachingStatus.ERR }
        videoCacheFiles.forEach { Log.d(TAG, "File is deleted ${it.delete()}") }
        Log.d(TAG, "Caching was interrupted by an error")
    }

    internal fun prepareForNewVideo() {
        _cachingStatusState.update { CachingStatus.NONE }
        _currentVideoMetadataState.update { VideoMetadata() }
    }

    private fun decrementQueueLen() =
        _videoCacheQueueLenState.update { maxOf(it - 1, 0) }

    internal fun resetCachingStatus() =
        _cachingStatusState.update { CachingStatus.NONE }
}