package com.paranid5.crescendo.system.services.video_cache.files

import com.paranid5.crescendo.caching.entity.VideoCacheData
import com.paranid5.crescendo.domain.metadata.model.VideoMetadata
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class VideoQueueManager {
    private val _videoQueueFlow = MutableSharedFlow<VideoCacheData>(extraBufferCapacity = 1000)

    val videoQueueFlow = _videoQueueFlow.asSharedFlow()

    private val _videoQueueLenState = MutableStateFlow(0)

    val videoQueueLenState = _videoQueueLenState.asStateFlow()

    private val _currentVideoMetadataState = MutableStateFlow(VideoMetadata())

    val currentVideoMetadataState = _currentVideoMetadataState.asStateFlow()

    fun resetVideoMetadata(videoMetadata: VideoMetadata) =
        _currentVideoMetadataState.update { videoMetadata }

    suspend fun offerNewVideo(videoCacheData: VideoCacheData) {
        _videoQueueFlow.emit(videoCacheData)
        _videoQueueLenState.update { it + 1 }
    }

    fun decrementQueueLen() = _videoQueueLenState.update { maxOf(it - 1, 0) }

    fun onCanceledAll() = _videoQueueLenState.update { 0 }
}
