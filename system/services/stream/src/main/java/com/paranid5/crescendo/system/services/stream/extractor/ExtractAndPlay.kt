package com.paranid5.crescendo.system.services.stream.extractor

import arrow.core.raise.either
import arrow.core.raise.ensure
import com.paranid5.crescendo.system.services.stream.StreamService
import com.paranid5.crescendo.system.services.stream.showErrNotificationAndSendBroadcast
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
        playerProvider.updateCurrentMetadata(metadata)
    }

    serviceScope.launch {
        playerProvider.updateStreamPlaybackPosition(initialPosition)
        playerProvider.playStreamViaPlayer(audioUrl, initialPosition)
    }
}
