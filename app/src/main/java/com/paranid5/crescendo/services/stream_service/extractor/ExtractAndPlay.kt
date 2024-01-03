package com.paranid5.crescendo.services.stream_service.extractor

import arrow.core.raise.either
import arrow.core.raise.ensure
import com.paranid5.crescendo.services.stream_service.StreamService
import com.paranid5.crescendo.services.stream_service.showErrNotificationAndSendBroadcast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal suspend inline fun StreamService.extractMediaFilesAndStartPlaying(
    ytUrl: String,
    initialPosition: Long,
) = either {
    val extractRes = urlExtractor.extractAudioUrlWithMeta(
        context = this@extractMediaFilesAndStartPlaying,
        ytUrl = ytUrl
    )

    ensure(extractRes.isRight()) {
        showErrNotificationAndSendBroadcast(extractRes.leftOrNull()!!)
    }

    val (audioUrl, metadata) = extractRes.getOrNull()!!

    serviceScope.launch(Dispatchers.IO) {
        mediaSessionManager.setCurrentMetadata(metadata)
    }

    serviceScope.launch {
        playerProvider.setStreamPlaybackPosition(initialPosition)
        playerProvider.playStreamViaPlayer(audioUrl, initialPosition)
    }
}