package com.paranid5.crescendo.services.stream_service.extractor

import com.paranid5.crescendo.domain.metadata.VideoMetadata
import com.paranid5.crescendo.services.stream_service.StreamService2
import com.paranid5.crescendo.services.stream_service.sendErrorBroadcast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

suspend inline fun StreamService2.extractMediaFilesAndStartPlaying(
    ytUrl: String,
    initialPosition: Long,
) {
    val extractRes = urlExtractor.extractAudioUrlWithMeta(this, ytUrl)

    if (extractRes.isFailure)
        return sendErrorBroadcast(extractRes.exceptionOrNull()!!)

    val (audioUrl, videoMeta) = extractRes.getOrNull()!!
    val metadata = videoMeta?.let(::VideoMetadata)

    serviceScope.launch(Dispatchers.IO) {
        mediaSessionManager.setCurrentMetadata(metadata)
    }

    serviceScope.launch {
        playerProvider.storeAndPlayStream(audioUrl, initialPosition)
    }
}