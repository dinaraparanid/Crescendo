package com.paranid5.crescendo.system.services.stream.media_session

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import com.paranid5.crescendo.core.media.images.getVideoCoverBitmapOrThumbnailAsync
import com.paranid5.crescendo.system.services.stream.StreamService
import com.paranid5.crescendo.utils.extensions.toAndroidMetadata
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal suspend fun StreamService.startMetadataMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        playerProvider
            .currentMetadataFlow
            .distinctUntilChanged()
            .map { it?.toAndroidMetadata(context = this@startMetadataMonitoring) }
            .collectLatest { metadata ->
                metadata?.let(mediaSessionManager::updateMetadata)
            }
    }

private suspend inline fun VideoMetadata.toAndroidMetadata(context: Context) =
    toAndroidMetadata(getVideoCoverBitmapOrThumbnailAsync(context = context, videoCovers = covers).await())