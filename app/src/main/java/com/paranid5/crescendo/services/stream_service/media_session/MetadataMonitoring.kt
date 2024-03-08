package com.paranid5.crescendo.services.stream_service.media_session

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import com.paranid5.crescendo.domain.utils.extensions.toAndroidMetadata
import com.paranid5.crescendo.media.images.getVideoCoverBitmapAsync
import com.paranid5.crescendo.services.stream_service.StreamService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

suspend fun StreamService.startMetadataMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        mediaSessionManager
            .currentMetadataFlow
            .distinctUntilChanged()
            .map { it?.toAndroidMetadata(context = this@startMetadataMonitoring) }
            .collectLatest { metadata ->
                metadata?.let(mediaSessionManager::updateMetadata)
            }
    }

private suspend inline fun com.paranid5.crescendo.core.common.metadata.VideoMetadata.toAndroidMetadata(context: Context) =
    toAndroidMetadata(getVideoCoverBitmapAsync(context, this).await())